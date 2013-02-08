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

package ru.ming13.gambit.provider;


import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import ru.ming13.gambit.db.DbOpenHelper;
import ru.ming13.gambit.db.DbSchema;


public class GambitProvider extends ContentProvider
{
	private SQLiteOpenHelper databaseHelper;

	private UriMatcher uriMatcher;

	@Override
	public boolean onCreate() {
		databaseHelper = new DbOpenHelper(getContext());

		uriMatcher = GambitProviderPaths.buildUriMatcher();

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {
			case GambitProviderPaths.Codes.DECKS:
				queryBuilder.setTables(DbSchema.Tables.DECKS);
				break;

			case GambitProviderPaths.Codes.DECK:
				queryBuilder.setTables(DbSchema.Tables.DECKS);
				queryBuilder.appendWhere(buildDeckSelectionClause(uri));
				break;

			case GambitProviderPaths.Codes.CARDS:
				queryBuilder.setTables(DbSchema.Tables.CARDS);
				queryBuilder.appendWhere(buildCardsSelectionClause(uri));
				break;

			case GambitProviderPaths.Codes.CARD:
				queryBuilder.setTables(DbSchema.Tables.CARDS);
				queryBuilder.appendWhere(buildCardSelectionClause(uri));
				break;

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}

		Cursor cursor = queryBuilder.query(databaseHelper.getReadableDatabase(), projection, selection,
			selectionArguments, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	private String buildDeckSelectionClause(Uri deckUri) {
		long deckId = GambitContract.Decks.getDeckId(deckUri);

		return buildSelectionClause(DbSchema.DecksColumns._ID, deckId);
	}

	private String buildSelectionClause(String fieldName, long id) {
		return String.format("%s = %d", fieldName, id);
	}

	private String buildCardsSelectionClause(Uri cardsUri) {
		long deckId = GambitContract.Cards.getDeckId(cardsUri);

		return buildSelectionClause(DbSchema.CardsColumns.DECK_ID, deckId);
	}

	private String buildCardSelectionClause(Uri cardUri) {
		long cardId = GambitContract.Cards.getCardId(cardUri);

		return buildSelectionClause(DbSchema.CardsColumns._ID, cardId);
	}

	private String buildUnsupportedUriDetailMessage(Uri unsupportedUri) {
		return String.format("Unsupported URI: %s", unsupportedUri.toString());
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		switch (uriMatcher.match(uri)) {
			case GambitProviderPaths.Codes.DECKS:
				return insertDeck(contentValues);

			case GambitProviderPaths.Codes.CARDS:
				return insertCard(uri, contentValues);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private Uri insertDeck(ContentValues deckValues) {
		if (!areDeckValuesValidForInsertion(deckValues)) {
			throw new IllegalArgumentException("Content values are not valid.");
		}

		if (!isDeckTitleUnique(deckValues)) {
			throw new DeckExistsException();
		}

		return createDeck(deckValues);
	}

	private boolean areDeckValuesValidForInsertion(ContentValues deckValues) {
		return deckValues.containsKey(DbSchema.DecksColumns.TITLE) && deckValues.containsKey(
			DbSchema.DecksColumns.CURRENT_CARD_INDEX);
	}

	private boolean isDeckTitleUnique(ContentValues deckValues) {
		String deckTitle = deckValues.getAsString(DbSchema.DecksColumns.TITLE);

		return queryDecksCount(deckTitle) == 0;
	}

	private long queryDecksCount(String deckTitle) {
		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		return DatabaseUtils.longForQuery(database, buildDecksCountQuery(deckTitle), null);
	}

	private String buildDecksCountQuery(String deckTitle) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("select count(%s) ", DbSchema.DecksColumns._ID));
		queryBuilder.append(String.format("from %s ", DbSchema.Tables.DECKS));
		queryBuilder.append(String.format("where upper(%s) = upper(%s)", DbSchema.DecksColumns.TITLE,
			DatabaseUtils.sqlEscapeString(deckTitle)));

		return queryBuilder.toString();
	}

	private Uri createDeck(ContentValues deckValues) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		long deckId = database.insert(DbSchema.Tables.DECKS, null, deckValues);

		Uri deckUri = GambitContract.Decks.buildDeckUri(deckId);
		getContext().getContentResolver().notifyChange(deckUri, null);

		return deckUri;
	}

	private Uri insertCard(Uri cardsUri, ContentValues cardValues) {
		if (!areCardValuesValidForInsertion(cardValues)) {
			throw new IllegalArgumentException("Content values are not valid.");
		}

		setCardInsertionDefaults(cardsUri, cardValues);

		return createCard(cardsUri, cardValues);
	}

	private boolean areCardValuesValidForInsertion(ContentValues cardValues) {
		return cardValues.containsKey(DbSchema.CardsColumns.FRONT_SIDE_TEXT) && cardValues.containsKey(
			DbSchema.CardsColumns.BACK_SIDE_TEXT);
	}

	private void setCardInsertionDefaults(Uri cardsUri, ContentValues cardValues) {
		long deckId = GambitContract.Cards.getDeckId(cardsUri);
		long cardOrderIndex = calculateCardOrderIndex(deckId);

		cardValues.put(DbSchema.CardsColumns.DECK_ID, deckId);
		cardValues.put(DbSchema.CardsColumns.ORDER_INDEX, cardOrderIndex);
	}

	private long calculateCardOrderIndex(long deckId) {
		if (isCardOrderIndexUsed(deckId)) {
			return queryCardsCount(deckId);
		}

		return DbSchema.CardsColumnsDefaultValues.ORDER_INDEX;
	}

	private boolean isCardOrderIndexUsed(long deckId) {
		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		return DatabaseUtils.longForQuery(database, buildMaximumCardsOrderIndexQuery(deckId),
			null) != DbSchema.CardsColumnsDefaultValues.ORDER_INDEX;
	}

	private String buildMaximumCardsOrderIndexQuery(long deckId) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("select max(%s) ", DbSchema.CardsColumns.ORDER_INDEX));
		queryBuilder.append(String.format("from %s ", DbSchema.Tables.CARDS));
		queryBuilder.append(String.format("where %s = %d", DbSchema.CardsColumns.DECK_ID, deckId));

		return queryBuilder.toString();
	}

	private long queryCardsCount(long deckId) {
		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		return DatabaseUtils.longForQuery(database, buildCardsCountQuery(deckId), null);
	}

	private String buildCardsCountQuery(long deckId) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("select count(%s) ", DbSchema.CardsColumns._ID));
		queryBuilder.append(String.format("from %s ", DbSchema.Tables.CARDS));
		queryBuilder.append(String.format("where %s = %d", DbSchema.CardsColumns.DECK_ID, deckId));

		return queryBuilder.toString();
	}

	private Uri createCard(Uri cardsUri, ContentValues cardValues) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		long cardId = database.insert(DbSchema.Tables.CARDS, null, cardValues);

		Uri cardUri = GambitContract.Cards.buildCardUri(cardsUri, cardId);
		getContext().getContentResolver().notifyChange(cardUri, null);

		return cardUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArguments) {
		switch (uriMatcher.match(uri)) {
			case GambitProviderPaths.Codes.DECK:
				return deleteDeck(uri);

			case GambitProviderPaths.Codes.CARD:
				return deleteCard(uri);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private int deleteDeck(Uri deckUri) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		int affectedRowsCount = database.delete(DbSchema.Tables.DECKS,
			buildDeckSelectionClause(deckUri), null);
		getContext().getContentResolver().notifyChange(deckUri, null);

		return affectedRowsCount;
	}

	private int deleteCard(Uri cardUri) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		int affectedRowsCount = database.delete(DbSchema.Tables.CARDS,
			buildCardSelectionClause(cardUri), null);
		getContext().getContentResolver().notifyChange(cardUri, null);

		return affectedRowsCount;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
		switch (uriMatcher.match(uri)) {
			case GambitProviderPaths.Codes.DECK:
				return updateDeck(uri, contentValues);

			case GambitProviderPaths.Codes.CARD:
				return updateCard(uri, contentValues);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private int updateDeck(Uri deckUri, ContentValues deckValues) {
		if (!areDeckValuesValidForUpdating(deckValues)) {
			throw new DeckExistsException();
		}

		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		int affectedRowsContent = database.update(DbSchema.Tables.DECKS, deckValues,
			buildDeckSelectionClause(deckUri), null);

		if (!isOnlyCurrentCardIndexUpdated(deckValues)) {
			getContext().getContentResolver().notifyChange(deckUri, null);
		}

		return affectedRowsContent;
	}

	private boolean areDeckValuesValidForUpdating(ContentValues deckValues) {
		if (!deckValues.containsKey(DbSchema.DecksColumns.TITLE)) {
			return true;
		}

		return isDeckTitleUnique(deckValues);
	}

	private boolean isOnlyCurrentCardIndexUpdated(ContentValues deckValues) {
		if (deckValues.containsKey(DbSchema.DecksColumns.TITLE)) {
			return false;
		}

		return deckValues.containsKey(DbSchema.DecksColumns.CURRENT_CARD_INDEX);
	}

	private int updateCard(Uri cardUri, ContentValues cardValues) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		int affectedRowsCount = database.update(DbSchema.Tables.CARDS, cardValues,
			buildCardSelectionClause(cardUri), null);
		getContext().getContentResolver().notifyChange(cardUri, null);

		return affectedRowsCount;
	}

	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		database.beginTransaction();
		try {
			ContentProviderResult[] results = new ContentProviderResult[operations.size()];

			for (int operationIndex = 0; operationIndex < operations.size(); operationIndex++) {
				ContentProviderOperation operation = operations.get(operationIndex);
				results[operationIndex] = operation.apply(this, results, operationIndex);
			}

			database.setTransactionSuccessful();

			return results;
		}
		finally {
			database.endTransaction();
		}
	}
}
