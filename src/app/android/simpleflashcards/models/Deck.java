package app.android.simpleflashcards.models;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Deck
{
	public static final int INVALID_CURRENT_CARD_INDEX = -1;

	private final SQLiteDatabase database;
	private final Decks parent;

	private int id;
	private String title;
	private int currentCardIndex;

	// Do not use the constructor. It should be used by Decks class only
	public Deck(SQLiteDatabase database, Decks parent, ContentValues values) {
		this.database = database;
		this.parent = parent;
		setValues(values);
	}

	private void setValues(ContentValues values) {
		Integer idAsInteger = values.getAsInteger(DbFieldNames.ID);
		if (idAsInteger == null) {
			throw new ModelsException();
		}
		id = idAsInteger;

		String titleAsString = values.getAsString(DbFieldNames.DECK_TITLE);
		if (titleAsString == null) {
			throw new ModelsException();
		}
		title = titleAsString;

		Integer currentCardIndexAsInteger = values.getAsInteger(DbFieldNames.DECK_CURRENT_CARD_INDEX);
		if (currentCardIndexAsInteger == null) {
			throw new ModelsException();
		}
		currentCardIndex = currentCardIndexAsInteger;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title.equals(this.title)) {
			return;
		}

		if (parent.containsDeckWithTitle(title)) {
			throw new AlreadyExistsException();
		}

		database.execSQL(buildTitleUpdatingQuery(title));
		this.title = title;
	}

	private String buildTitleUpdatingQuery(String newTitle) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s set ", DbTableNames.DECKS));
		builder.append(String.format("%s = '%s' ", DbFieldNames.DECK_TITLE, newTitle));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	public int getId() {
		return id;
	}

	public boolean isEmpty() {
		return getCardsCount() == 0;
	}

	public int getCardsCount() {
		Cursor cursor = database.rawQuery(buildCardsCountSelectionQuery(), null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public int getCurrentCardIndex() {
		return currentCardIndex;
	}

	public void setCurrentCardIndex(int index) {
		if (index == currentCardIndex) {
			return;
		}

		database.execSQL(buildCurrentCardIndexUpdatingQuery(index));
		currentCardIndex = index;
	}

	private String buildCurrentCardIndexUpdatingQuery(int index) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s set ", DbTableNames.DECKS));
		builder.append(String.format("%s = %d ", DbFieldNames.DECK_CURRENT_CARD_INDEX, index));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	private String buildCardsCountSelectionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select count(*) from %s ", DbTableNames.CARDS));
		builder.append(String.format("where %s = %d ", DbFieldNames.CARD_DECK_ID, id));

		return builder.toString();
	}

	public List<Card> getCardsList() {
		List<Card> cardsList = new ArrayList<Card>();

		Cursor cursor = database
			.rawQuery(buildCardsSelectionQuery(DbFieldNames.CARD_ORDER_INDEX), null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			ContentValues values = contentValuesFromCursor(cursor);
			cardsList.add(new Card(database, values));
			cursor.moveToNext();
		}

		return cardsList;
	}

	private ContentValues contentValuesFromCursor(Cursor cursor) {
		ContentValues values = new ContentValues(cursor.getCount());

		int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
		values.put(DbFieldNames.ID, id);

		int deckId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.CARD_DECK_ID));
		values.put(DbFieldNames.CARD_DECK_ID, deckId);

		String frontSideText = cursor.getString(cursor
			.getColumnIndexOrThrow(DbFieldNames.CARD_FRONT_SIDE_TEXT));
		values.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, frontSideText);

		String backSideText = cursor.getString(cursor
			.getColumnIndexOrThrow(DbFieldNames.CARD_BACK_SIDE_TEXT));
		values.put(DbFieldNames.CARD_BACK_SIDE_TEXT, backSideText);

		int orderIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.CARD_ORDER_INDEX));
		values.put(DbFieldNames.CARD_ORDER_INDEX, orderIndex);

		return values;
	}

	public Card addNewCard(String frontSideText, String backSideText) {
		database.beginTransaction();
		try {
			database.execSQL(buildCardInsertionQuery(frontSideText, backSideText));
			setCurrentCardIndex(0);

			database.setTransactionSuccessful();

			return getCardById(lastInsertedId());
		}
		finally {
			database.endTransaction();
		}
	}

	private String buildCardInsertionQuery(String frontSideText, String backSideText) {
		// Append to the end
		int newCardOrderIndex = getCardsCount();

		StringBuilder builder = new StringBuilder();

		builder.append(String.format("insert into %s ", DbTableNames.CARDS));

		builder.append("( ");
		builder.append(String.format("%s, ", DbFieldNames.CARD_DECK_ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbFieldNames.CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbFieldNames.CARD_ORDER_INDEX));
		builder.append(") ");

		builder.append("values ( ");
		builder.append(String.format("%d, ", id));
		builder.append(String.format("'%s', ", frontSideText));
		builder.append(String.format("'%s', ", backSideText));
		builder.append(String.format("%d ", newCardOrderIndex));
		builder.append(")");

		return builder.toString();
	}

	private int lastInsertedId() {
		Cursor cursor = database.rawQuery(buildLastInsertedIdSelectionQuery(), null);
		if (!cursor.moveToFirst()) {
			throw new ModelsException();
		}
		return cursor.getInt(0);
	}

	private String buildLastInsertedIdSelectionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");
		builder.append(String.format("max(%s) ", DbFieldNames.ID));
		builder.append(String.format("from %s ", DbTableNames.CARDS));

		return builder.toString();
	}

	public Card getCardById(int id) {
		Cursor cursor = database.rawQuery(buildCardByIdSelectionQuery(id), null);
		if (!cursor.moveToFirst()) {
			throw new ModelsException(String.format("There's no a card with id = %d in database", id));
		}

		return new Card(database, contentValuesFromCursor(cursor));
	}

	private String buildCardByIdSelectionQuery(int id) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_DECK_ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbFieldNames.CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbFieldNames.CARD_ORDER_INDEX));

		builder.append(String.format("from %s ", DbTableNames.CARDS));

		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	public void deleteCard(Card card) {
		database.beginTransaction();

		try {
			database.execSQL(buildCardDeletingQuery(card));
			if (getCardsCount() == 0) {
				setCurrentCardIndex(INVALID_CURRENT_CARD_INDEX);
			}
			else {
				setCurrentCardIndex(0);
			}

			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private String buildCardDeletingQuery(Card card) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("delete from %s ", DbTableNames.CARDS));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, card.getId()));

		return builder.toString();
	}

	public void shuffleCards() {
		database.beginTransaction();
		try {
			tryShuffleCards();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryShuffleCards() {
		Cursor cursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.CARD_FRONT_SIDE_TEXT),
			null);
		if (cursor.getCount() == 0) {
			return;
		}

		CardsOrderShuffler shuffler = new CardsOrderShuffler(cursor.getCount());
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, shuffler.generateNextIndex());
			cursor.moveToNext();
		}
	}

	public void resetCardsOrder() {
		database.beginTransaction();
		try {
			tryResetCardsOrder();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryResetCardsOrder() {
		Cursor cursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.CARD_FRONT_SIDE_TEXT),
			null);
		if (cursor.getCount() == 0) {
			return;
		}

		int index = 0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, index);
			index++;
			cursor.moveToNext();
		}
	}

	private String buildCardsSelectionQuery(String orderByField) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_DECK_ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbFieldNames.CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbFieldNames.CARD_ORDER_INDEX));

		builder.append(String.format("from %s ", DbTableNames.CARDS));

		builder.append("where ");

		builder.append(String.format("%s = %d ", DbFieldNames.CARD_DECK_ID, id));

		builder.append(String.format("order by %s", orderByField));

		return builder.toString();
	}

	private void setCardOrderIndex(int cardId, int index) {
		database.execSQL(buildCardOrderIndexSettingQuery(cardId, index));
	}

	private String buildCardOrderIndexSettingQuery(int cardId, int index) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s ", DbTableNames.CARDS));
		builder.append(String.format("set %s = %d ", DbFieldNames.CARD_ORDER_INDEX, index));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, cardId));

		return builder.toString();
	}

	@Override
	public int hashCode() {
		// hashCode() is not intended to be used
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object otherObject) {
		if (this == otherObject) {
			return true;
		}

		if (!(otherObject instanceof Deck)) {
			return false;
		}

		Deck otherDeck = (Deck) otherObject;

		if (id != otherDeck.id) {
			return false;
		}

		boolean titlesAreEqual = title == null ? otherDeck.title == null : title
			.equals(otherDeck.title);
		if (!titlesAreEqual) {
			return false;
		}

		return true;
	}

}
