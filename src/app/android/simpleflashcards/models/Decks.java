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
		Cursor cursor = database.rawQuery(buildDecksCountSelectionQuery(), null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private String buildDecksCountSelectionQuery() {
		return String.format("select count(*) from %s", DbTableNames.DECKS);
	}

	public List<Deck> getDecksList() {
		List<Deck> decksList = new ArrayList<Deck>();

		Cursor cursor = database.rawQuery(buildDecksSelectionQuery(), null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContentValues values = contentValuesFromCursor(cursor);
			decksList.add(new Deck(database, this, values));
			cursor.moveToNext();
		}

		return decksList;
	}

	private String buildDecksSelectionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");
		builder.append(String.format("%s, %s ", DbFieldNames.ID, DbFieldNames.DECK_TITLE));
		builder.append(String.format("from %s ", DbTableNames.DECKS));
		builder.append(String.format("order by %s", DbFieldNames.DECK_TITLE));

		return builder.toString();
	}

	private ContentValues contentValuesFromCursor(Cursor cursor) {
		ContentValues values = new ContentValues();

		int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
		values.put(DbFieldNames.ID, id);

		String title = cursor.getString(cursor.getColumnIndexOrThrow(DbFieldNames.DECK_TITLE));
		values.put(DbFieldNames.DECK_TITLE, title);

		return values;
	}

	public Deck addNewDeck(String title) {
		if (containsDeckWithTitle(title)) {
			throw new AlreadyExistsException();
		}
		database.execSQL(buildDeckInsertionQuery(title));

		return getDeckById(lastInsertedId());
	}

	private String buildDeckInsertionQuery(String deckTitle) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("insert into %s ", DbTableNames.DECKS));
		builder.append(String.format("(%s) ", DbFieldNames.DECK_TITLE));
		builder.append(String.format("values ('%s') ", deckTitle));

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
		builder.append(String.format("from %s ", DbTableNames.DECKS));

		return builder.toString();
	}

	public Deck getDeckById(int id) {
		Cursor cursor = database.rawQuery(buildDeckByIdSelectionQuery(id), null);
		if (!cursor.moveToFirst()) {
			throw new ModelsException(String.format("There's no a deck with id = %d in database",
				id));
		}

		return new Deck(database, this, contentValuesFromCursor(cursor));
	}

	private String buildDeckByIdSelectionQuery(int id) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");
		builder.append(String.format("%s, %s ", DbFieldNames.ID, DbFieldNames.DECK_TITLE));
		builder.append(String.format("from %s ", DbTableNames.DECKS));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	public Deck getDeckByCardId(int cardId) {
		Cursor cursor = database.rawQuery(buildDeckByCardIdSelectionQuery(cardId), null);
		if (!cursor.moveToFirst()) {
			throw new ModelsException(String.format(
				"There's no a deck that is a parent for card with id = %d", cardId));
		}

		return new Deck(database, this, contentValuesFromCursor(cursor));
	}

	private String buildDeckByCardIdSelectionQuery(int cardId) {
		StringBuilder innerSelectBuilder = new StringBuilder();

		innerSelectBuilder.append(String.format("select %s from %s ", DbFieldNames.CARD_DECK_ID,
			DbTableNames.CARDS));
		innerSelectBuilder.append(String.format("where %s = %d", DbFieldNames.ID, cardId));

		StringBuilder builder = new StringBuilder();

		builder.append("select ");
		builder.append(String.format("%s, %s ", DbFieldNames.ID, DbFieldNames.DECK_TITLE));
		builder.append(String.format("from %s ", DbTableNames.DECKS));
		builder.append(String.format("where %s = (%s)", DbFieldNames.ID,
			innerSelectBuilder.toString()));

		return builder.toString();
	}

	public void deleteDeck(Deck deck) {
		database.execSQL(buildDeckDeletingQuery(deck));
	}

	private String buildDeckDeletingQuery(Deck deck) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("delete from %s ", DbTableNames.DECKS));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, deck.getId()));

		return builder.toString();
	}

	public boolean containsDeckWithTitle(String title) {
		Cursor cursor = database.rawQuery(buildDeckWithTitlePresenceQuery(title), null);
		cursor.moveToFirst();

		return cursor.getInt(0) > 0;
	}

	private String buildDeckWithTitlePresenceQuery(String title) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select count(*) from %s ", DbTableNames.DECKS));
		builder.append(String.format("where upper(%s) = upper('%s')", DbFieldNames.DECK_TITLE,
			title));

		return builder.toString();
	}
}
