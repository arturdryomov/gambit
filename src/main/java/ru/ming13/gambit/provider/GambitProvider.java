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

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.ArrayList;

import ru.ming13.gambit.database.DatabaseOpenHelper;
import ru.ming13.gambit.database.DatabaseSchema;
import ru.ming13.gambit.util.SqlBuilder;

public class GambitProvider extends ContentProvider
{
	private SQLiteOpenHelper databaseHelper;
	private UriMatcher uriMatcher;

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseOpenHelper(getContext());
		uriMatcher = GambitUriMatcher.getMatcher();

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArguments, String sortOrder) {
		Cursor cursor = buildQueryBuilder(uri).query(
			databaseHelper.getReadableDatabase(),
			projection,
			selection,
			selectionArguments,
			null,
			null,
			sortOrder);

		cursor.setNotificationUri(getContentResolver(), uri);

		return cursor;
	}

	private SQLiteQueryBuilder buildQueryBuilder(Uri uri) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {
			case GambitUriMatcher.Codes.DECKS:
				queryBuilder.setTables(DatabaseSchema.Tables.DECKS);
				break;

			case GambitUriMatcher.Codes.DECK:
				queryBuilder.setTables(DatabaseSchema.Tables.DECKS);
				queryBuilder.appendWhere(buildDeckSelectionClause(uri));
				break;

			case GambitUriMatcher.Codes.CARDS:
				queryBuilder.setTables(DatabaseSchema.Tables.CARDS);
				queryBuilder.appendWhere(buildCardsSelectionClause(uri));
				break;

			case GambitUriMatcher.Codes.CARD:
				queryBuilder.setTables(DatabaseSchema.Tables.CARDS);
				queryBuilder.appendWhere(buildCardSelectionClause(uri));
				break;

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}

		return queryBuilder;
	}

	private String buildDeckSelectionClause(Uri deckUri) {
		long deckId = GambitContract.Decks.getDeckId(deckUri);

		return SqlBuilder.buildSelectionClause(DatabaseSchema.DecksColumns._ID, deckId);
	}

	private String buildCardsSelectionClause(Uri cardsUri) {
		long deckId = GambitContract.Cards.getDeckId(cardsUri);

		return SqlBuilder.buildSelectionClause(DatabaseSchema.CardsColumns.DECK_ID, deckId);
	}

	private String buildCardSelectionClause(Uri cardUri) {
		long cardId = GambitContract.Cards.getCardId(cardUri);

		return SqlBuilder.buildSelectionClause(DatabaseSchema.CardsColumns._ID, cardId);
	}

	private String buildUnsupportedUriDetailMessage(Uri unsupportedUri) {
		return String.format("Unsupported URI: %s", unsupportedUri.toString());
	}

	private ContentResolver getContentResolver() {
		return getContext().getContentResolver();
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		Uri insertedContentsUri = insertContents(uri, contentValues);

		getContentResolver().notifyChange(insertedContentsUri, null);

		return insertedContentsUri;
	}

	private Uri insertContents(Uri uri, ContentValues contentValues) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
			case GambitUriMatcher.Codes.DECKS:
				return insertDeck(database, contentValues);

			case GambitUriMatcher.Codes.CARDS:
				return insertCard(database, uri, contentValues);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private Uri insertDeck(SQLiteDatabase database, ContentValues deckValues) {
		long deckId = database.insert(DatabaseSchema.Tables.DECKS, null, deckValues);

		return GambitContract.Decks.getDeckUri(deckId);
	}

	private Uri insertCard(SQLiteDatabase database, Uri cardsUri, ContentValues cardValues) {
		long cardId = database.insert(DatabaseSchema.Tables.CARDS, null, cardValues);

		return GambitContract.Cards.getCardUri(cardsUri, cardId);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArguments) {
		int deletedCount = deleteContents(uri);

		getContentResolver().notifyChange(uri, null);

		return deletedCount;
	}

	private int deleteContents(Uri uri) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
			case GambitUriMatcher.Codes.DECK:
				return deleteDeck(database, uri);

			case GambitUriMatcher.Codes.CARD:
				return deleteCard(database, uri);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private int deleteDeck(SQLiteDatabase database, Uri deckUri) {
		return database.delete(DatabaseSchema.Tables.DECKS, buildDeckSelectionClause(deckUri), null);
	}

	private int deleteCard(SQLiteDatabase database, Uri cardUri) {
		return database.delete(DatabaseSchema.Tables.CARDS, buildCardSelectionClause(cardUri), null);
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
		int updatedCount = updateContents(uri, contentValues);

		getContentResolver().notifyChange(uri, null);

		return updatedCount;
	}

	private int updateContents(Uri uri, ContentValues contentValues) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
			case GambitUriMatcher.Codes.DECK:
				return updateDeck(database, uri, contentValues);

			case GambitUriMatcher.Codes.CARD:
				return updateCard(database, uri, contentValues);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private int updateDeck(SQLiteDatabase database, Uri deckUri, ContentValues deckValues) {
		try {
			return database.update(DatabaseSchema.Tables.DECKS, deckValues, buildDeckSelectionClause(deckUri), null);
		} catch (SQLiteException e) {
			throw new RuntimeException(e);
		}
	}

	private int updateCard(SQLiteDatabase database, Uri cardUri, ContentValues cardValues) {
		try {
			return database.update(DatabaseSchema.Tables.CARDS, cardValues, buildCardSelectionClause(cardUri), null);
		} catch (SQLiteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		try {
			database.beginTransaction();

			ContentProviderResult[] results = new ContentProviderResult[operations.size()];

			for (int operationPosition = 0; operationPosition < operations.size(); operationPosition++) {
				ContentProviderOperation operation = operations.get(operationPosition);
				results[operationPosition] = operation.apply(this, results, operationPosition);
			}

			database.setTransactionSuccessful();

			return results;
		} finally {
			database.endTransaction();
		}
	}
}
