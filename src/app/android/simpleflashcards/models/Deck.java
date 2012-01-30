package app.android.simpleflashcards.models;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Deck
{
	private SQLiteDatabase database;
	private int id;
	private String title;
	private Decks parent;

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

	public int getCardsCount() {
		Cursor cursor = database.rawQuery(buildCardsCountSelectionQuery(), null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private String buildCardsCountSelectionQuery() {
		return String.format("select count(*) from %s", DbTableNames.CARDS);
	}

	public List<Card> getCardsList() {
		List<Card> cardsList = new ArrayList<Card>();

		Cursor cursor = database.rawQuery(buildCardsSelectionQuery(), null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			ContentValues values = contentValuesFromCursor(cursor);
			cardsList.add(new Card(database, values));
			cursor.moveToNext();
		}

		return cardsList;
	}

	private String buildCardsSelectionQuery() {
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

		builder.append(String.format("order by %s", DbFieldNames.CARD_ORDER_INDEX));

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
		database.execSQL(buildCardInsertionQuery(frontSideText, backSideText));
		return readCardById(lastInsertedId());
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
		builder.append(String.format("max(%s) ", DbFieldNames.ID, DbFieldNames.ID));
		builder.append(String.format("from %s ", DbTableNames.CARDS));

		return builder.toString();
	}

	private Card readCardById(int id) {
		Cursor cursor = database.rawQuery(buildCardByIdSelectionQuery(id), null);
		if (!cursor.moveToFirst()) {
			throw new ModelsException(
				String.format("There's no decks with id = %d in database", id));
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

	public void deleteCard(Card card) {
		database.execSQL(buildCardDeletingQuery(card));
	}

	private String buildCardDeletingQuery(Card card) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("delete from %s ", DbTableNames.CARDS));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, card.getId()));

		return builder.toString();
	}

	public void shuffleCards() {
		Cursor cursor = database.rawQuery(buildCardsAlphabeticalSelectionQuery(), null);
		if (cursor.getCount() == 0) {
			return;
		}

		CardsOrderIndexGenerator generator = new CardsOrderIndexGenerator(cursor.getCount());
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, generator.generate());
			cursor.moveToNext();
		}
	}

	public void resetCardsOrder() {
		Cursor cursor = database.rawQuery(buildCardsAlphabeticalSelectionQuery(), null);
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

	private String buildCardsAlphabeticalSelectionQuery() {
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

		builder.append(String.format("order by %s", DbFieldNames.CARD_FRONT_SIDE_TEXT));

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

}
