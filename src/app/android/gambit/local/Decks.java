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

		while (cursor.moveToNext()) {
			ContentValues values = contentValuesFromCursor(cursor);
			decksList.add(new Deck(values));
		}

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

		int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
		values.put(DbFieldNames.ID, id);

		String title = cursor.getString(cursor.getColumnIndexOrThrow(DbFieldNames.DECK_TITLE));
		values.put(DbFieldNames.DECK_TITLE, title);

		int currentCardIndex = cursor.getInt(cursor
			.getColumnIndexOrThrow(DbFieldNames.DECK_CURRENT_CARD_INDEX));
		values.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, currentCardIndex);

		return values;
	}

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

		database.execSQL(buildDeckInsertionQuery(title));
		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();

		return getDeckById(lastInsertedId());
	}

	public boolean containsDeckWithTitle(String title) {
		Cursor cursor = database.rawQuery(buildDeckWithTitlePresenceQuery(title), null);
		cursor.moveToFirst();

		return cursor.getInt(0) > 0;
	}

	private String buildDeckWithTitlePresenceQuery(String title) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select count(*) from %s ", DbTableNames.DECKS));
		builder.append(String.format("where upper(%s) = upper('%s')", DbFieldNames.DECK_TITLE, title));

		return builder.toString();
	}

	private String buildDeckInsertionQuery(String deckTitle) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("insert into %s ", DbTableNames.DECKS));
		builder.append(String.format("(%s, %s) ", DbFieldNames.DECK_TITLE,
			DbFieldNames.DECK_CURRENT_CARD_INDEX));
		builder.append(String.format("values ('%s', %d) ", deckTitle, Deck.INVALID_CURRENT_CARD_INDEX));

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
			throw new ModelsException(String.format("There's no a deck with id = %d in database", id));
		}

		return new Deck(contentValuesFromCursor(cursor));
	}

	private String buildDeckByIdSelectionQuery(int id) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.DECK_TITLE));
		builder.append(String.format("%s ", DbFieldNames.DECK_CURRENT_CARD_INDEX));

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

		return new Deck(contentValuesFromCursor(cursor));
	}

	private String buildDeckByCardIdSelectionQuery(int cardId) {
		StringBuilder innerSelectBuilder = new StringBuilder();

		innerSelectBuilder.append(String.format("select %s from %s ", DbFieldNames.CARD_DECK_ID,
			DbTableNames.CARDS));
		innerSelectBuilder.append(String.format("where %s = %d", DbFieldNames.ID, cardId));

		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.DECK_TITLE));
		builder.append(String.format("%s ", DbFieldNames.DECK_CURRENT_CARD_INDEX));

		builder.append(String.format("from %s ", DbTableNames.DECKS));
		builder
			.append(String.format("where %s = (%s)", DbFieldNames.ID, innerSelectBuilder.toString()));

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
		database.execSQL(buildDeckCardsDeleteingQuery(deck));
		database.execSQL(buildDeckDeletingQuery(deck));
		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private String buildDeckCardsDeleteingQuery(Deck deck) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("delete from %s ", DbTableNames.CARDS));
		builder.append(String.format("where %s = %d", DbFieldNames.CARD_DECK_ID, deck.getId()));

		return builder.toString();
	}

	private String buildDeckDeletingQuery(Deck deck) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("delete from %s ", DbTableNames.DECKS));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, deck.getId()));

		return builder.toString();
	}

	public InternetDateTime getLastUpdatedDateTime() {
		return lastUpdateDateTimeHandler.getLastUpdatedDateTime();
	}
}
