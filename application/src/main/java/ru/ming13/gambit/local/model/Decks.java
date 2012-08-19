/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.local.model;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import ru.ming13.gambit.local.DbException;
import ru.ming13.gambit.local.sqlite.DbFieldNames;
import ru.ming13.gambit.local.DbProvider;
import ru.ming13.gambit.local.sqlite.DbTableNames;
import ru.ming13.gambit.local.LastUpdateDateTimeHandler;
import ru.ming13.gambit.remote.InternetDateTime;


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

		DatabaseUtils.cursorLongToContentValues(databaseCursor, DbFieldNames.ID, databaseValues);
		DatabaseUtils.cursorStringToContentValues(databaseCursor, DbFieldNames.DECK_TITLE,
			databaseValues);
		DatabaseUtils.cursorIntToContentValues(databaseCursor, DbFieldNames.DECK_CURRENT_CARD_INDEX,
			databaseValues);

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

	boolean containsDeckWithTitle(String title) {
		String presenceQuery = buildDeckWithTitlePresenceQuery(title);

		return DatabaseUtils.longForQuery(database, presenceQuery, null) > 0;
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

	private Deck getDeckById(long id) {
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
