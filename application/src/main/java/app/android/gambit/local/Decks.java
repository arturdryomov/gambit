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

	public List<Deck> getDecksList() {
		List<Deck> decksList = new ArrayList<Deck>();

		Cursor databaseCursor = database.rawQuery(buildDecksSelectionQuery(), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractDeckDatabaseValues(databaseCursor);
			decksList.add(new Deck(databaseValues));
		}

		databaseCursor.close();

		return decksList;
	}

	private String buildDecksSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.DECK_TITLE));
		queryBuilder.append(String.format("%s ", DbFieldNames.DECK_CURRENT_CARD_INDEX));

		queryBuilder.append(String.format("from %s ", DbTableNames.DECKS));
		queryBuilder.append(String.format("order by %s", DbFieldNames.DECK_TITLE));

		return queryBuilder.toString();
	}

	private ContentValues extractDeckDatabaseValues(Cursor databaseCursor) {
		ContentValues databaseValues = new ContentValues();

		long id = databaseCursor.getLong(databaseCursor.getColumnIndexOrThrow(DbFieldNames.ID));
		databaseValues.put(DbFieldNames.ID, id);

		String title = databaseCursor.getString(
			databaseCursor.getColumnIndexOrThrow(DbFieldNames.DECK_TITLE));
		databaseValues.put(DbFieldNames.DECK_TITLE, title);

		int currentCardIndex = databaseCursor.getInt(
			databaseCursor.getColumnIndexOrThrow(DbFieldNames.DECK_CURRENT_CARD_INDEX));
		databaseValues.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, currentCardIndex);

		return databaseValues;
	}

	/**
	 * @throws AlreadyExistsException if deck with such title already exists.
	 */
	public Deck createDeck(String title) {
		database.beginTransaction();
		try {
			Deck deck = tryCreateDeck(title);
			database.setTransactionSuccessful();
			return deck;
		}
		finally {
			database.endTransaction();
		}
	}

	private Deck tryCreateDeck(String title) {
		if (containsDeckWithTitle(title)) {
			throw new AlreadyExistsException();
		}

		Deck deck = getDeckById(insertDeckWithTitle(title));
		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();

		return deck;
	}

	public boolean containsDeckWithTitle(String title) {
		Cursor databaseCursor = database.rawQuery(buildDeckWithTitlePresenceQuery(title), null);
		databaseCursor.moveToFirst();

		boolean contains = databaseCursor.getInt(0) > 0;

		databaseCursor.close();

		return contains;
	}

	private String buildDeckWithTitlePresenceQuery(String title) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("select count(*) from %s ", DbTableNames.DECKS));
		queryBuilder.append(
			String.format("where upper(%s) = upper('%s')", DbFieldNames.DECK_TITLE, title));

		return queryBuilder.toString();
	}

	private long insertDeckWithTitle(String title) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.DECK_TITLE, title);
		databaseValues.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, Deck.INVALID_CURRENT_CARD_INDEX);

		return database.insert(DbTableNames.DECKS, null, databaseValues);
	}

	/**
	 * @throws DbException if there is no deck with id specified.
	 */
	public Deck getDeckById(long id) {
		Cursor databaseCursor = database.rawQuery(buildDeckByIdSelectionQuery(id), null);
		if (!databaseCursor.moveToFirst()) {
			throw new DbException(String.format("There's no a deck with id = %d in database", id));
		}

		Deck deck = new Deck(extractDeckDatabaseValues(databaseCursor));

		databaseCursor.close();

		return deck;
	}

	private String buildDeckByIdSelectionQuery(long id) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.DECK_TITLE));
		queryBuilder.append(String.format("%s ", DbFieldNames.DECK_CURRENT_CARD_INDEX));

		queryBuilder.append(String.format("from %s ", DbTableNames.DECKS));
		queryBuilder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return queryBuilder.toString();
	}

	/**
	 * @throws DbException if there is no card with id specified.
	 */
	public Deck getDeckByCardId(long cardId) {
		Cursor databaseCursor = database.rawQuery(buildDeckByCardIdSelectionQuery(cardId), null);
		if (!databaseCursor.moveToFirst()) {
			throw new DbException(
				String.format("There's no a deck that is a parent for card with id = %d", cardId));
		}

		Deck deck = new Deck(extractDeckDatabaseValues(databaseCursor));

		databaseCursor.close();

		return deck;
	}

	private String buildDeckByCardIdSelectionQuery(long cardId) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s.%s as %2$s, ", DbTableNames.DECKS, DbFieldNames.ID));
		queryBuilder.append(
			String.format("%s.%s as %2$s, ", DbTableNames.DECKS, DbFieldNames.DECK_TITLE));
		queryBuilder.append(
			String.format("%s.%s as %2$s ", DbTableNames.DECKS, DbFieldNames.DECK_CURRENT_CARD_INDEX));

		queryBuilder.append(
			String.format("from %s inner join %s on %1$s.%s = %2$s.%s  ", DbTableNames.DECKS,
				DbTableNames.CARDS, DbFieldNames.ID, DbFieldNames.CARD_DECK_ID));

		queryBuilder.append(
			String.format("where %s.%s = %d", DbTableNames.CARDS, DbFieldNames.ID, cardId));

		return queryBuilder.toString();
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
