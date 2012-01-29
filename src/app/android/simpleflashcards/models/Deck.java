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
		Integer idAsInteger = values.getAsInteger(DbConstants.FIELD_ID);
		if (idAsInteger == null) {
			throw new ModelsException();
		}
		id = idAsInteger;

		String titleAsString = values.getAsString(DbConstants.FIELD_DECK_TITLE);
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
			throw new ModelsException(String.format("There is already a deck with title '%s'",
				title));
		}

		database.execSQL(buildTitleUpdatingQuery(title));
		this.title = title;
	}

	private String buildTitleUpdatingQuery(String newTitle) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s set ", DbConstants.TABLE_DECKS));
		builder.append(String.format("%s = '%s' ", DbConstants.FIELD_DECK_TITLE, newTitle));
		builder.append(String.format("where %s = %d", DbConstants.FIELD_ID, id));

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
		return String.format("select count(*) from %s", DbConstants.TABLE_CARDS);
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

		builder.append(String.format("%s, ", DbConstants.FIELD_ID));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_DECK_ID));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbConstants.FIELD_CARD_ORDER_INDEX));

		builder.append(String.format("from %s ", DbConstants.TABLE_CARDS));

		builder.append("where ");

		builder.append(String.format("%s = %d ", DbConstants.FIELD_CARD_DECK_ID, id));

		builder.append(String.format("order by %s", DbConstants.FIELD_CARD_ORDER_INDEX));

		return builder.toString();
	}

	private ContentValues contentValuesFromCursor(Cursor cursor) {
		ContentValues values = new ContentValues(cursor.getCount());

		int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.FIELD_ID));
		values.put(DbConstants.FIELD_ID, id);

		int deckId = cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.FIELD_CARD_DECK_ID));
		values.put(DbConstants.FIELD_CARD_DECK_ID, deckId);

		String frontSideText = cursor.getString(cursor
			.getColumnIndexOrThrow(DbConstants.FIELD_CARD_FRONT_SIDE_TEXT));
		values.put(DbConstants.FIELD_CARD_FRONT_SIDE_TEXT, frontSideText);

		String backSideText = cursor.getString(cursor
			.getColumnIndexOrThrow(DbConstants.FIELD_CARD_BACK_SIDE_TEXT));
		values.put(DbConstants.FIELD_CARD_BACK_SIDE_TEXT, backSideText);

		int orderIndex = cursor.getInt(cursor
			.getColumnIndexOrThrow(DbConstants.FIELD_CARD_ORDER_INDEX));
		values.put(DbConstants.FIELD_CARD_ORDER_INDEX, orderIndex);

		return values;
	}

	public void addNewCard(String frontSideText, String backSideText) {
		database.execSQL(buildCardInsertionQuery(frontSideText, backSideText));
	}

	private String buildCardInsertionQuery(String frontSideText, String backSideText) {
		// Append to the end
		int newCardOrderIndex = getCardsCount();

		StringBuilder builder = new StringBuilder();

		builder.append(String.format("insert into %s ", DbConstants.TABLE_CARDS));

		builder.append("( ");
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_DECK_ID));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbConstants.FIELD_CARD_ORDER_INDEX));
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

		builder.append(String.format("delete from %s ", DbConstants.TABLE_CARDS));
		builder.append(String.format("where %s = %d", DbConstants.FIELD_ID, card.getId()));

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
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.FIELD_ID));
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
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.FIELD_ID));
			setCardOrderIndex(cardId, index);
			index++;
			cursor.moveToNext();
		}
	}

	private String buildCardsAlphabeticalSelectionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbConstants.FIELD_ID));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_DECK_ID));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbConstants.FIELD_CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbConstants.FIELD_CARD_ORDER_INDEX));

		builder.append(String.format("from %s ", DbConstants.TABLE_CARDS));

		builder.append("where ");

		builder.append(String.format("%s = %d ", DbConstants.FIELD_CARD_DECK_ID, id));

		builder.append(String.format("order by %s", DbConstants.FIELD_CARD_FRONT_SIDE_TEXT));

		return builder.toString();
	}

	private void setCardOrderIndex(int cardId, int index) {
		database.execSQL(buildCardOrderIndexSettingQuery(cardId, index));
	}

	private String buildCardOrderIndexSettingQuery(int cardId, int index) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s ", DbConstants.TABLE_CARDS));
		builder.append(String.format("set %s = %d ", DbConstants.FIELD_CARD_ORDER_INDEX, index));
		builder.append(String.format("where %s = %d", DbConstants.FIELD_ID, cardId));

		return builder.toString();
	}

}
