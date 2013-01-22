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


public class GambitProvider extends ContentProvider
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
				selection = buildDeckSelectionClause(uri);
				break;

			case ProviderUris.Codes.CARDS:
				queryBuilder.setTables(DbTableNames.CARDS);
				selection = buildDeckCardsSelectionClause(uri);
				break;

			case ProviderUris.Codes.CARD:
				queryBuilder.setTables(DbTableNames.CARDS);
				selection = buildCardSelectionClause(uri);
				break;

			default:
				throw new IllegalArgumentException("Unsupported URI.");
		}

		Cursor decksCursor = queryBuilder.query(databaseHelper.getReadableDatabase(), projection,
			selection, selectionArguments, null, null, sortOrder);

		decksCursor.setNotificationUri(getContext().getContentResolver(), uri);

		return decksCursor;
	}

	private String buildDeckSelectionClause(Uri deckUri) {
		long deckId = ContentUris.parseId(deckUri);

		return String.format("%s = %d", DbFieldNames.ID, deckId);
	}

	private String buildDeckCardsSelectionClause(Uri cardsUri) {
		long deckId = ProviderUris.Content.parseDeckId(cardsUri);

		return String.format("%s = %d", DbFieldNames.CARD_DECK_ID, deckId);
	}

	private String buildCardSelectionClause(Uri cardUri) {
		long cardId = ContentUris.parseId(cardUri);

		return String.format("%s = %d", DbFieldNames.ID, cardId);
	}

	@Override
	public String getType(Uri uri) {
		return null;
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

		return queryDecksCount(deckTitle) == 0;
	}

	private long queryDecksCount(String deckTitle) {
		return DatabaseUtils.longForQuery(databaseHelper.getReadableDatabase(),
			buildDecksCountQuery(deckTitle), null);
	}

	private String buildDecksCountQuery(String deckTitle) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(
			String.format("select count(%s) from %s ", DbFieldNames.ID, DbTableNames.DECKS));
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
		long cardDeckId = ProviderUris.Content.parseDeckId(cardsUri);
		long cardOrderIndex = queryDeckCardsCount(cardDeckId);

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
		switch (ProviderUris.MATCHER.match(uri)) {
			case ProviderUris.Codes.DECK:
				return deleteDeck(uri);

			case ProviderUris.Codes.CARD:
				return deleteCard(uri);

			default:
				throw new IllegalArgumentException("Unsupported URI.");
		}
	}

	private int deleteDeck(Uri deckUri) {
		int affectedRowsCount = databaseHelper.getWritableDatabase().delete(DbTableNames.DECKS,
			buildDeckSelectionClause(deckUri), null);
		getContext().getContentResolver().notifyChange(deckUri, null);

		return affectedRowsCount;
	}

	private int deleteCard(Uri cardUri) {
		int affectedRowsCount = databaseHelper.getWritableDatabase().delete(DbTableNames.CARDS,
			buildCardSelectionClause(cardUri), null);
		getContext().getContentResolver().notifyChange(cardUri, null);

		return affectedRowsCount;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
		switch (ProviderUris.MATCHER.match(uri)) {
			case ProviderUris.Codes.DECK:
				return updateDeck(uri, contentValues);

			case ProviderUris.Codes.CARD:
				return updateCard(uri, contentValues);

			default:
				throw new IllegalArgumentException("Invalid URI.");
		}
	}

	private int updateDeck(Uri deckUri, ContentValues deckValues) {
		if (!areDeckValuesValid(deckValues)) {
			throw new IllegalArgumentException("Content values are not valid.");
		}

		if (!isDeckTitleUnique(deckValues)) {
			throw new DeckExistsException();
		}

		return editDeck(deckUri, deckValues);
	}

	private int editDeck(Uri deckUri, ContentValues deckValues) {
		int affectedRowsContent = databaseHelper.getWritableDatabase().update(DbTableNames.DECKS,
			deckValues, buildDeckSelectionClause(deckUri), null);
		getContext().getContentResolver().notifyChange(deckUri, null);

		return affectedRowsContent;
	}

	private int updateCard(Uri cardUri, ContentValues cardValues) {
		if (!areCardValuesValid(cardValues)) {
			throw new IllegalArgumentException("Content values are not valid.");
		}

		return editCard(cardUri, cardValues);
	}

	private int editCard(Uri cardUri, ContentValues cardValues) {
		int affectedRowsCount = databaseHelper.getWritableDatabase().update(DbTableNames.CARDS,
			cardValues, buildCardSelectionClause(cardUri), null);
		getContext().getContentResolver().notifyChange(cardUri, null);

		return affectedRowsCount;
	}
}
