package ru.ming13.gambit.local.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import ru.ming13.gambit.local.sqlite.DbFieldNames;
import ru.ming13.gambit.local.sqlite.DbOpenHelper;
import ru.ming13.gambit.local.sqlite.DbTableNames;


public class DecksProvider extends ContentProvider
{
	private static final int DEFAULT_CURRENT_CARD_INDEX = 0;

	private SQLiteOpenHelper databaseHelper;

	@Override
	public boolean onCreate() {
		databaseHelper = new DbOpenHelper(getContext());

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (ProviderUris.MATCHER.match(uri)) {
			case ProviderUris.Codes.DECKS:
				queryBuilder.setTables(DbTableNames.DECKS);
				break;

			case ProviderUris.Codes.DECK:
				queryBuilder.setTables(DbTableNames.DECKS);
				selection = buildDeckWhereClause(uri);
				break;

			case ProviderUris.Codes.CARDS:
				queryBuilder.setTables(DbTableNames.CARDS);
				selection = buildCardsWhereClause(uri);
				break;

			default:
				throw new IllegalArgumentException("Unsupported URI.");
		}

		Cursor decksCursor = queryBuilder.query(databaseHelper.getReadableDatabase(), projection,
			selection, selectionArguments, null, null, sortOrder);

		decksCursor.setNotificationUri(getContext().getContentResolver(), uri);

		return decksCursor;
	}

	private String buildDeckWhereClause(Uri deckUri) {
		long deckId = ContentUris.parseId(deckUri);

		return String.format("%s = %d", DbFieldNames.ID, deckId);
	}

	private String buildCardsWhereClause(Uri cardsUri) {
		return String.format("%s = %d", DbFieldNames.CARD_DECK_ID, parseDeckId(cardsUri));
	}

	private long parseDeckId(Uri cardsUri) {
		String deckId = cardsUri.getPathSegments().get(1);

		return Long.parseLong(deckId);
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		switch (ProviderUris.MATCHER.match(uri)) {
			case ProviderUris.Codes.DECKS:
				return insertDeck(contentValues);

			case ProviderUris.Codes.CARDS:
				return insertCard(uri, contentValues);

			default:
				throw new IllegalArgumentException("Unsupported URI.");
		}
	}

	private Uri insertDeck(ContentValues contentValues) {
		if (!areDeckValuesValid(contentValues)) {
			throw new IllegalArgumentException("Content values are not valid.");
		}

		if (!isDeckTitleUnique(contentValues)) {
			throw new DeckExistsException();
		}

		setDeckValuesDefaults(contentValues);

		return createDeck(contentValues);
	}

	private boolean areDeckValuesValid(ContentValues deckValues) {
		return deckValues.containsKey(DbFieldNames.DECK_TITLE);
	}

	private boolean isDeckTitleUnique(ContentValues deckValues) {
		String deckTitle = deckValues.getAsString(DbFieldNames.DECK_TITLE);

		return DatabaseUtils.longForQuery(databaseHelper.getReadableDatabase(),
			buildDecksCountQuery(deckTitle), null) == 0;
	}

	private String buildDecksCountQuery(String deckTitle) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("select count(%s) ", DbFieldNames.ID));
		queryBuilder.append(String.format("from %s ", DbTableNames.DECKS));
		queryBuilder.append(
			String.format("where upper(%s) = upper('%s')", DbFieldNames.DECK_TITLE, deckTitle));

		return queryBuilder.toString();
	}


	private void setDeckValuesDefaults(ContentValues deckValues) {
		deckValues.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, DEFAULT_CURRENT_CARD_INDEX);
	}

	private Uri createDeck(ContentValues deckValues) {
		long deckId = databaseHelper.getWritableDatabase().insert(DbTableNames.DECKS, null, deckValues);

		Uri deckUri = ProviderUris.Content.buildDeckUri(deckId);
		getContext().getContentResolver().notifyChange(deckUri, null);

		return deckUri;
	}

	private Uri insertCard(Uri cardsUri, ContentValues cardValues) {
		if (!areCardValuesValid(cardValues)) {
			throw new IllegalArgumentException("Content values are not valid.");
		}

		setCardValuesDefaults(cardsUri, cardValues);

		return createCard(cardsUri, cardValues);
	}

	private boolean areCardValuesValid(ContentValues cardValues) {
		return cardValues.containsKey(DbFieldNames.CARD_FRONT_SIDE_TEXT) && cardValues.containsKey(
			DbFieldNames.CARD_BACK_SIDE_TEXT);
	}

	private void setCardValuesDefaults(Uri cardsUri, ContentValues cardValues) {
		long cardDeckId = parseDeckId(cardsUri);
		long cardOrderIndex = queryDeckCardsCount(parseDeckId(cardsUri));

		cardValues.put(DbFieldNames.CARD_DECK_ID, cardDeckId);
		cardValues.put(DbFieldNames.CARD_ORDER_INDEX, cardOrderIndex);
	}

	private long queryDeckCardsCount(long deckId) {
		return DatabaseUtils.longForQuery(databaseHelper.getReadableDatabase(),
			buildCardsCountQuery(deckId), null);
	}

	private String buildCardsCountQuery(long deckId) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(
			String.format("select count(%s) from %s ", DbFieldNames.ID, DbTableNames.CARDS));
		queryBuilder.append(String.format("where %s = %d", DbFieldNames.CARD_DECK_ID, deckId));

		return queryBuilder.toString();
	}

	private Uri createCard(Uri cardsUri, ContentValues cardValues) {
		long cardId = databaseHelper.getWritableDatabase().insert(DbTableNames.CARDS, null, cardValues);

		Uri cardUri = ProviderUris.Content.buildCardUri(cardsUri, cardId);
		getContext().getContentResolver().notifyChange(cardUri, null);

		return cardUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArguments) {
		if (ProviderUris.MATCHER.match(uri) != ProviderUris.Codes.DECK) {
			throw new IllegalArgumentException("Unsupported URI.");
		}

		return deleteDeck(uri);
	}

	private int deleteDeck(Uri deckUri) {
		int affectedRowsCount = databaseHelper.getWritableDatabase().delete(DbTableNames.DECKS,
			buildDeckWhereClause(deckUri), null);
		getContext().getContentResolver().notifyChange(deckUri, null);

		return affectedRowsCount;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
		if (ProviderUris.MATCHER.match(uri) != ProviderUris.Codes.DECK) {
			throw new IllegalArgumentException("Invalid URI.");
		}

		if (!areDeckValuesValid(contentValues)) {
			throw new IllegalArgumentException("Content values are not valid.");
		}

		if (!isDeckTitleUnique(contentValues)) {
			throw new DeckExistsException();
		}

		return updateDeck(uri, contentValues);
	}

	private int updateDeck(Uri deckUri, ContentValues deckValues) {
		int affectedRowsContent = databaseHelper.getWritableDatabase().update(DbTableNames.DECKS,
			deckValues, buildDeckWhereClause(deckUri), null);
		getContext().getContentResolver().notifyChange(deckUri, null);

		return affectedRowsContent;
	}
}
