package app.android.gambit.local;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Deck implements Parcelable
{
	public static final int INVALID_CURRENT_CARD_INDEX = -1;

	private final SQLiteDatabase database;
	private final Decks decks;
	private final LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	private int id;
	private String title;
	private int currentCardIndex;

	Deck(ContentValues values) {
		database = DatabaseProvider.getInstance().getDatabase();
		decks = DatabaseProvider.getInstance().getDecks();
		lastUpdateDateTimeHandler = DatabaseProvider.getInstance().getLastUpdateTimeHandler();

		setValues(values);
	}

	private void setValues(ContentValues values) {
		Integer idAsInteger = values.getAsInteger(DbFieldNames.ID);
		if (idAsInteger == null) {
			throw new DatabaseException();
		}
		id = idAsInteger;

		String titleAsString = values.getAsString(DbFieldNames.DECK_TITLE);
		if (titleAsString == null) {
			throw new DatabaseException();
		}
		title = titleAsString;

		Integer currentCardIndexAsInteger = values.getAsInteger(DbFieldNames.DECK_CURRENT_CARD_INDEX);
		if (currentCardIndexAsInteger == null) {
			throw new DatabaseException();
		}
		currentCardIndex = currentCardIndexAsInteger;
	}

	public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>() {
		public Deck createFromParcel(Parcel parcel) {
			return new Deck(parcel);
		}

		public Deck[] newArray(int size) {
			return new Deck[size];
		}
	};

	private Deck(Parcel parcel) {
		database = DatabaseProvider.getInstance().getDatabase();
		decks = DatabaseProvider.getInstance().getDecks();
		lastUpdateDateTimeHandler = DatabaseProvider.getInstance().getLastUpdateTimeHandler();

		readFromParcel(parcel);
	}

	public void readFromParcel(Parcel parcel) {
		id = parcel.readInt();
		title = parcel.readString();
		currentCardIndex = parcel.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(id);
		parcel.writeString(title);
		parcel.writeInt(currentCardIndex);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		database.beginTransaction();
		try {
			trySetTitle(title);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetTitle(String title) {
		if (title.equals(this.title)) {
			return;
		}

		if (decks.containsDeckWithTitle(title)) {
			throw new AlreadyExistsException();
		}

		database.execSQL(buildTitleUpdatingQuery(title));
		this.title = title;

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
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

	private String buildCardsCountSelectionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select count(*) from %s ", DbTableNames.CARDS));
		builder.append(String.format("where %s = %d ", DbFieldNames.CARD_DECK_ID, id));

		return builder.toString();
	}

	public int getCurrentCardIndex() {
		return currentCardIndex;
	}

	public void setCurrentCardIndex(int index) {
		database.beginTransaction();
		try {
			trySetCurrentCardIndex(index);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetCurrentCardIndex(int index) {
		if (index == currentCardIndex) {
			return;
		}

		database.execSQL(buildCurrentCardIndexUpdatingQuery(index));
		currentCardIndex = index;

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private String buildCurrentCardIndexUpdatingQuery(int index) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s set ", DbTableNames.DECKS));
		builder.append(String.format("%s = %d ", DbFieldNames.DECK_CURRENT_CARD_INDEX, index));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	public List<Card> getCardsList() {
		List<Card> cardsList = new ArrayList<Card>();

		Cursor cursor = database
			.rawQuery(buildCardsSelectionQuery(DbFieldNames.CARD_ORDER_INDEX), null);

		while (cursor.moveToNext()) {
			ContentValues values = contentValuesFromCursor(cursor);
			cardsList.add(new Card(values));
		}

		return cardsList;
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
			Card newCard = tryAddNewCard(frontSideText, backSideText);
			database.setTransactionSuccessful();
			return newCard;
		}
		finally {
			database.endTransaction();
		}
	}

	private Card tryAddNewCard(String frontSideText, String backSideText) {
		database.execSQL(buildCardInsertionQuery(frontSideText, backSideText));
		setCurrentCardIndex(0);

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();

		return getCardById(lastInsertedId());
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
			throw new DatabaseException();
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
			throw new DatabaseException(String.format("There's no a card with id = %d in database", id));
		}

		return new Card(contentValuesFromCursor(cursor));
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
			tryDeleteCard(card);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryDeleteCard(Card card) {
		database.execSQL(buildCardDeletingQuery(card));
		if (getCardsCount() == 0) {
			setCurrentCardIndex(INVALID_CURRENT_CARD_INDEX);
		}
		else {
			setCurrentCardIndex(0);
		}

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
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
		List<Integer> currentCardOrderIndexes = getCurrentCardOrderIndexes();
		if (currentCardOrderIndexes.size() <= 1) {
			return;
		}

		List<Integer> newCardOrderIndexes;

		if (currentCardOrderIndexes.size() == 2) {
			newCardOrderIndexes = currentCardOrderIndexes;
			Collections.swap(newCardOrderIndexes, 0, 1);
		}

		else {
			newCardOrderIndexes = new ArrayList<Integer>(currentCardOrderIndexes);
			while (newCardOrderIndexes.equals(currentCardOrderIndexes)) {
				Collections.shuffle(newCardOrderIndexes);
			}
		}

		setCardsOrder(newCardOrderIndexes);

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private List<Integer> getCurrentCardOrderIndexes() {
		Cursor cursor = database
			.rawQuery(buildCardsSelectionQuery(DbFieldNames.CARD_ORDER_INDEX), null);

		List<Integer> cardOrderIndexes = new ArrayList<Integer>();

		while (cursor.moveToNext()) {
			int index = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.CARD_ORDER_INDEX));
			cardOrderIndexes.add(index);
		}

		return cardOrderIndexes;
	}

	private void setCardsOrder(List<Integer> cardsOrderIndexes) {
		Cursor cursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.ID), null);

		if (cursor.getCount() != cardsOrderIndexes.size()) {
			throw new DatabaseException();
		}

		for (int index : cardsOrderIndexes) {
			cursor.moveToNext();
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, index);
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
		Cursor cursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.ID), null);
		if (cursor.getCount() == 0) {
			return;
		}

		int index = 0;
		while (cursor.moveToNext()) {
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, index);
			index++;
		}

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
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

		if ((title == null) && (otherDeck.title != null)) {
			return false;
		}

		if ((title != null) && !title.equals(otherDeck.title)) {
			return false;
		}

		return true;
	}
}
