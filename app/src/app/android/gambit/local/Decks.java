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
		database = DbProvider.getInstance().getDatabase();
		lastUpdateDateTimeHandler = DbProvider.getInstance().getLastUpdateTimeHandler();
	}

	public int getDecksCount() {
		Cursor cursor = database.rawQuery(buildDecksCountSelectionQuery(), null);
		cursor.moveToFirst();
		int decksCount = cursor.getInt(0);
		cursor.close();

		return decksCount;
	}

	private String buildDecksCountSelectionQuery() {
		return String.format("select count(*) from %s", DbTableNames.DECKS);
	}

	public List<Deck> getDecksList() {
		List<Deck> decksList = new ArrayList<Deck>();

		Cursor cursor = database.rawQuery(buildDecksSelectionQuery(), null);

		while (cursor.moveToNext()) {
			ContentValues values = contentValuesFromCursor(cursor);
			decksList.add(new Deck(values));
		}

		cursor.close();

		return decksList;
	}

	private String buildDecksSelectionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.DECK_TITLE));
		builder.append(String.format("%s ", DbFieldNames.DECK_CURRENT_CARD_INDEX));

		builder.append(String.format("from %s ", DbTableNames.DECKS));
		builder.append(String.format("order by %s", DbFieldNames.DECK_TITLE));

		return builder.toString();
	}

	private ContentValues contentValuesFromCursor(Cursor cursor) {
		ContentValues values = new ContentValues();

		long id = cursor.getLong(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
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
		Cursor cursor = database.rawQuery(buildDeckWithTitlePresenceQuery(title), null);
		cursor.moveToFirst();

		boolean contains = cursor.getInt(0) > 0;

		cursor.close();

		return contains;
	}

	private String buildDeckWithTitlePresenceQuery(String title) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select count(*) from %s ", DbTableNames.DECKS));
		builder.append(String.format("where upper(%s) = upper('%s')", DbFieldNames.DECK_TITLE, title));

		return builder.toString();
	}

	private long insertDeckWithTitle(String title) {
		ContentValues columns = new ContentValues();
		columns.put(DbFieldNames.DECK_TITLE, title);
		columns.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, Deck.INVALID_CURRENT_CARD_INDEX);

		return database.insert(DbTableNames.DECKS, null, columns);
	}

	/**
	 * @throws DbException if there is no deck with id specified.
	 */
	public Deck getDeckById(long id) {
		Cursor cursor = database.rawQuery(buildDeckByIdSelectionQuery(id), null);
		if (!cursor.moveToFirst()) {
			throw new DbException(String.format("There's no a deck with id = %d in database", id));
		}

		Deck deck = new Deck(contentValuesFromCursor(cursor));

		cursor.close();

		return deck;
	}

	private String buildDeckByIdSelectionQuery(long id) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.DECK_TITLE));
		builder.append(String.format("%s ", DbFieldNames.DECK_CURRENT_CARD_INDEX));

		builder.append(String.format("from %s ", DbTableNames.DECKS));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	/**
	 * @throws DbException if there is no card with id specified.
	 */
	public Deck getDeckByCardId(long cardId) {
		Cursor cursor = database.rawQuery(buildDeckByCardIdSelectionQuery(cardId), null);
		if (!cursor.moveToFirst()) {
			throw new DbException(String.format(
				"There's no a deck that is a parent for card with id = %d", cardId));
		}

		Deck deck = new Deck(contentValuesFromCursor(cursor));

		cursor.close();

		return deck;
	}

	private String buildDeckByCardIdSelectionQuery(long cardId) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s.%s as %2$s, ", DbTableNames.DECKS, DbFieldNames.ID));
		builder.append(String.format("%s.%s as %2$s, ", DbTableNames.DECKS, DbFieldNames.DECK_TITLE));
		builder.append(String.format("%s.%s as %2$s ", DbTableNames.DECKS,
			DbFieldNames.DECK_CURRENT_CARD_INDEX));

		builder.append(String.format("from %s inner join %s on %1$s.%s = %2$s.%s  ",
			DbTableNames.DECKS, DbTableNames.CARDS, DbFieldNames.ID, DbFieldNames.CARD_DECK_ID));

		builder.append(String.format("where %s.%s = %d", DbTableNames.CARDS, DbFieldNames.ID, cardId));

		return builder.toString();
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

	public void clear() {
		database.beginTransaction();
		try {
			tryClear();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryClear() {
		database.delete(DbTableNames.CARDS, null, null);
		database.delete(DbTableNames.DECKS, null, null);
	}

	public InternetDateTime getLastUpdatedDateTime() {
		return lastUpdateDateTimeHandler.getLastUpdatedDateTime();
	}

	public void setCurrentDateTimeAsLastUpdated() {
		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
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
