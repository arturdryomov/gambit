package app.android.simpleflashcards.models;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Decks
{
	private SQLiteDatabase database;

	public Decks(SQLiteDatabase database) {
		this.database = database;
	}

	public int getDecksCount() {
		Cursor cursor = database.rawQuery(buildSelectDecksCountQuery(), null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private String buildSelectDecksCountQuery() {
		return String.format("select count(*) from %s", DbConstants.TABLE_DECKS);
	}

	public List<Deck> getDecksList() {
		List<Deck> decksList = new ArrayList<Deck>();

		Cursor cursor = database.rawQuery(buildSelectDecksQuery(), null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			ContentValues values = contentValuesFromCursor(cursor);
			decksList.add(new Deck(database, this, values));
			cursor.moveToNext();
		}

		return decksList;
	}

	private String buildSelectDecksQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");
		builder
			.append(String.format("%s, %s ", DbConstants.FIELD_ID, DbConstants.FIELD_DECK_TITLE));
		builder.append(String.format("from %s ", DbConstants.TABLE_DECKS));
		builder.append(String.format("order by %s", DbConstants.FIELD_DECK_TITLE));

		return builder.toString();
	}

	private ContentValues contentValuesFromCursor(Cursor cursor) {
		ContentValues values = new ContentValues(cursor.getCount());

		int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.FIELD_ID));
		values.put(DbConstants.FIELD_ID, id);

		String title = cursor.getString(cursor.getColumnIndexOrThrow(DbConstants.FIELD_DECK_TITLE));
		values.put(DbConstants.FIELD_DECK_TITLE, title);

		return values;
	}

	public void addNewDeck(String title) {
		if (containsDeckWithTitle(title)) {
			throw new ModelsException(String.format("There is already a deck with title '%s'",
				title));
		}
		database.execSQL(buildInsertDeckQuery(title));
	}

	private String buildInsertDeckQuery(String deckTitle) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("insert into %s ", DbConstants.TABLE_DECKS));
		builder.append(String.format("(%s) ", DbConstants.FIELD_DECK_TITLE));
		builder.append(String.format("values ('%s') ", deckTitle));

		return builder.toString();
	}

	public void deleteDeck(Deck deck) {
		database.execSQL(buildDeleteDeckQuery(deck));
	}

	private String buildDeleteDeckQuery(Deck deck) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("delete from %s ", DbConstants.TABLE_DECKS));
		builder.append(String.format("where %s = %d", DbConstants.FIELD_ID, deck.getId()));

		return builder.toString();
	}

	public boolean containsDeckWithTitle(String title) {
		Cursor cursor = database.rawQuery(buildContainsDeckWithTitleQuery(title), null);
		cursor.moveToFirst();

		return cursor.getInt(0) > 0;
	}

	private String buildContainsDeckWithTitleQuery(String title) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select count(*) from %s ", DbConstants.TABLE_DECKS));
		builder.append(String.format("where upper(%s) = upper('%s')", DbConstants.FIELD_DECK_TITLE,
			title));

		return builder.toString();
	}
}
