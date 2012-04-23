package app.android.gambit.local;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.android.gambit.InternetDateTime;


public class Decks
{
	private final SQLiteDatabase database;
	private final LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	public Decks() {
		database = DatabaseProvider.getInstance().getDatabase();
		lastUpdateDateTimeHandler = DatabaseProvider.getInstance().getLastUpdateTimeHandler();
	}

	public int getDecksCount() {
		Cursor cursor = queryDecksCount();
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private Cursor queryDecksCount() {
		return database.query(DbTableNames.DECKS, new String[] { "count(*)" }, null, null, null, null,
			null);
	}

	public List<Deck> getDecksList() {
		List<Deck> decksList = new ArrayList<Deck>();

		Cursor cursor = queryDecksList();

		while (cursor.moveToNext()) {
			ContentValues values = contentValuesFromCursor(cursor);
			decksList.add(new Deck(values));
		}

		return decksList;
	}

	private Cursor queryDecksList() {
		// TODO: Code formatter makes array initializaton ugly. This comments, though,
		// make code look a bit strange as well

		// @formatter:off
		String[] columns = {
			DbFieldNames.ID,
			DbFieldNames.DECK_TITLE,
			DbFieldNames.DECK_CURRENT_CARD_INDEX };
		// @formatter:on

		return database.query(DbTableNames.DECKS, columns, null, null, null, null,
			DbFieldNames.DECK_TITLE);
	}

	private ContentValues contentValuesFromCursor(Cursor cursor) {
		ContentValues values = new ContentValues();

		int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
		values.put(DbFieldNames.ID, id);

		String title = cursor.getString(cursor.getColumnIndexOrThrow(DbFieldNames.DECK_TITLE));
		values.put(DbFieldNames.DECK_TITLE, title);

		int currentCardIndex = cursor.getInt(cursor
			.getColumnIndexOrThrow(DbFieldNames.DECK_CURRENT_CARD_INDEX));
		values.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, currentCardIndex);

		return values;
	}

	/**
	 *	@throws AlreadyExistsException if deck with such title already exists.
	 */
	public Deck addNewDeck(String title) {
		database.beginTransaction();
		try {
			Deck newDeck = tryAddNewDeck(title);
			database.setTransactionSuccessful();
			return newDeck;
		}
		finally {
			database.endTransaction();
		}
	}

	private Deck tryAddNewDeck(String title) {
		if (containsDeckWithTitle(title)) {
			throw new AlreadyExistsException();
		}

		Deck insertedDeck = getDeckById(insertDeckWithTitle(title));
		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();

		return insertedDeck;
	}

	public boolean containsDeckWithTitle(String title) {
		Cursor cursor = queryDecksCountWithTitle(title);
		cursor.moveToFirst();

		return cursor.getInt(0) > 0;
	}

	private Cursor queryDecksCountWithTitle(String title) {
		return database.query(DbTableNames.DECKS, new String[] { "count(*)" }, "upper(?) = upper(?)",
			new String[] { DbFieldNames.DECK_TITLE, title }, null, null, null);
	}

	private long insertDeckWithTitle(String title) {
		ContentValues columns = new ContentValues();
		columns.put(DbFieldNames.DECK_TITLE, title);
		columns.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, Deck.INVALID_CURRENT_CARD_INDEX);

		return database.insert(DbTableNames.DECKS, null, columns);
	}

	/**
	 * @throws DatabaseException if there is no deck with id specified.
	 */
	public Deck getDeckById(long id) {
		Cursor cursor = queryDeckById(id);
		if (!cursor.moveToFirst()) {
			throw new DatabaseException(String.format("There's no a deck with id = %d in database", id));
		}

		return new Deck(contentValuesFromCursor(cursor));
	}

	private Cursor queryDeckById(long id) {
		// @formatter:off
		String[] columns = {
			DbFieldNames.ID,
			DbFieldNames.DECK_TITLE,
			DbFieldNames.DECK_CURRENT_CARD_INDEX };
		// @formatter:on

		return database.query(DbTableNames.DECKS, columns,
			String.format("%s = %d", DbFieldNames.ID, id), null, null, null, null);
	}

	/**
	 * @throws DatabaseException if there is no card with id specified.
	 */
	public Deck getDeckByCardId(int cardId) {
		//		Cursor cursor = database.rawQuery(buildDeckByCardIdSelectionQuery(cardId), null);
		Cursor cursor = queryDeckByCardId(cardId);
		if (!cursor.moveToFirst()) {
			throw new DatabaseException(String.format(
				"There's no a deck that is a parent for card with id = %d", cardId));
		}

		return new Deck(contentValuesFromCursor(cursor));
	}

	private Cursor queryDeckByCardId(long id) {
		// @formatter:off
		String[] columns = {
			String.format("%s.%s as %2$s", DbTableNames.DECKS, DbFieldNames.ID),
			String.format("%s.%s as %2$s", DbTableNames.DECKS, DbFieldNames.DECK_TITLE),
			String.format("%s.%s as %2$s", DbTableNames.DECKS, DbFieldNames.DECK_CURRENT_CARD_INDEX)
		};


		String tables = String.format("%s inner join %s on %1$s.%s = %2$s.%s",
			DbTableNames.DECKS,
			DbTableNames.CARDS,
			DbFieldNames.ID,
			DbFieldNames.CARD_DECK_ID);
		// @formatter:on

		String where = String.format("%s.%s = %d", DbTableNames.CARDS, DbFieldNames.ID, id);

		return database.query(tables, columns, where, null, null, null, null);
	}

	public void deleteDeck(Deck deck) {
		database.beginTransaction();
		try {
			tryDeleteDeck(deck);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryDeleteDeck(Deck deck) {
		database.delete(DbTableNames.CARDS,
			String.format("%s = %d", DbFieldNames.CARD_DECK_ID, deck.getId()), null);
		database.delete(DbTableNames.DECKS, String.format("%s = %d", DbFieldNames.ID, deck.getId()),
			null);

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	public InternetDateTime getLastUpdatedDateTime() {
		return lastUpdateDateTimeHandler.getLastUpdatedDateTime();
	}

	public void beginTransaction() {
		database.beginTransaction();
	}

	public void setTransactionSuccessful() {
		database.setTransactionSuccessful();
	}

	public void endTransaction() {
		database.endTransaction();
	}
}
