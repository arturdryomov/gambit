package app.android.gambit.local;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Deck implements Parcelable
{
	public static final int INVALID_CURRENT_CARD_INDEX = -1;
	private static final int SPECIAL_PARCELABLE_OBJECTS_BITMASK = 0;

	private final SQLiteDatabase database;
	private final Decks decks;
	private final LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	private long id;
	private String title;
	private int currentCardIndex;

	Deck(ContentValues databaseValues) {
		database = DbProvider.getInstance().getDatabase();
		decks = DbProvider.getInstance().getDecks();
		lastUpdateDateTimeHandler = DbProvider.getInstance().getLastUpdateTimeHandler();

		setValues(databaseValues);
	}

	private void setValues(ContentValues databaseValues) {
		Long idAsLong = databaseValues.getAsLong(DbFieldNames.ID);
		if (idAsLong == null) {
			throw new DbException();
		}
		id = idAsLong.longValue();

		String titleAsString = databaseValues.getAsString(DbFieldNames.DECK_TITLE);
		if (titleAsString == null) {
			throw new DbException();
		}
		title = titleAsString;

		Integer currentCardIndexAsInteger = databaseValues.getAsInteger(
			DbFieldNames.DECK_CURRENT_CARD_INDEX);
		if (currentCardIndexAsInteger == null) {
			throw new DbException();
		}
		currentCardIndex = currentCardIndexAsInteger;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @throws AlreadyExistsException if deck with such title already exists.
	 */
	public void setTitle(String title) {
		database.beginTransaction();
		try {
			trySetTitle(title);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetTitle(String title) {
		if (title.equals(this.title)) {
			return;
		}

		if (decks.containsDeckWithTitle(title)) {
			throw new AlreadyExistsException();
		}

		updateTitle(title);
		this.title = title;

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private void updateTitle(String title) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.DECK_TITLE, title);

		database.update(DbTableNames.DECKS, databaseValues, buildDeckSelectionClause(), null);
	}

	private String buildDeckSelectionClause() {
		return String.format("%s = %d", DbFieldNames.ID, id);
	}

	public long getId() {
		return id;
	}

	public boolean isEmpty() {
		return getCardsCount() == 0;
	}

	public long getCardsCount() {
		String cardsCountingQuery = buildCardsCountSelectionQuery();

		return DatabaseUtils.longForQuery(database, cardsCountingQuery, null);
	}

	private String buildCardsCountSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("select count(*) from %s ", DbTableNames.CARDS));
		queryBuilder.append(String.format("where %s = %d", DbFieldNames.CARD_DECK_ID, id));

		return queryBuilder.toString();
	}

	public int getCurrentCardIndex() {
		return currentCardIndex;
	}

	public void setCurrentCardIndex(int index) {
		database.beginTransaction();
		try {
			trySetCurrentCardIndex(index);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetCurrentCardIndex(int index) {
		if (index == currentCardIndex) {
			return;
		}

		updateCurrentCardIndex(index);
		currentCardIndex = index;

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private void updateCurrentCardIndex(int index) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, index);

		database.update(DbTableNames.DECKS, databaseValues, buildDeckSelectionClause(), null);
	}

	public List<Card> getCardsList() {
		List<Card> cardsList = new ArrayList<Card>();

		Cursor databaseCursor = database.rawQuery(
			buildCardsSelectionQuery(DbFieldNames.CARD_ORDER_INDEX), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractCardDatabaseValuesFromCursor(databaseCursor);
			cardsList.add(new Card(databaseValues));
		}

		databaseCursor.close();

		return cardsList;
	}

	private String buildCardsSelectionQuery(String orderByField) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.CARD_DECK_ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT));
		queryBuilder.append(String.format("%s, ", DbFieldNames.CARD_BACK_SIDE_TEXT));
		queryBuilder.append(String.format("%s ", DbFieldNames.CARD_ORDER_INDEX));

		queryBuilder.append(String.format("from %s ", DbTableNames.CARDS));

		queryBuilder.append("where ");

		queryBuilder.append(String.format("%s = %d ", DbFieldNames.CARD_DECK_ID, id));

		queryBuilder.append(String.format("order by %s", orderByField));

		return queryBuilder.toString();
	}

	private ContentValues extractCardDatabaseValuesFromCursor(Cursor databaseCursor) {
		ContentValues databaseValues = new ContentValues(databaseCursor.getCount());

		DatabaseUtils.cursorLongToContentValues(databaseCursor, DbFieldNames.ID, databaseValues);
		DatabaseUtils.cursorStringToContentValues(databaseCursor, DbFieldNames.CARD_FRONT_SIDE_TEXT,
			databaseValues);
		DatabaseUtils.cursorStringToContentValues(databaseCursor, DbFieldNames.CARD_BACK_SIDE_TEXT,
			databaseValues);

		return databaseValues;
	}

	public Card createCard(String frontSideText, String backSideText) {
		database.beginTransaction();
		try {
			Card card = tryCreateCard(frontSideText, backSideText);
			database.setTransactionSuccessful();
			return card;
		}
		finally {
			database.endTransaction();
		}
	}

	private Card tryCreateCard(String frontSideText, String backSideText) {
		Card card = getCardById(insertCard(frontSideText, backSideText));
		setCurrentCardIndex(0);

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();

		return card;
	}

	private long insertCard(String frontSideText, String backSideText) {
		long newCardOrderIndex = getCardsCount();

		ContentValues databaseValues = new ContentValues();

		databaseValues.put(DbFieldNames.CARD_DECK_ID, id);
		databaseValues.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, frontSideText);
		databaseValues.put(DbFieldNames.CARD_BACK_SIDE_TEXT, backSideText);
		databaseValues.put(DbFieldNames.CARD_ORDER_INDEX, newCardOrderIndex);

		return database.insert(DbTableNames.CARDS, null, databaseValues);
	}

	private Card getCardById(long id) {
		Cursor databaseCursor = database.rawQuery(buildCardByIdSelectionQuery(id), null);
		if (!databaseCursor.moveToFirst()) {
			throw new DbException(String.format("There's no a card with id = %d in database", id));
		}

		Card card = new Card(extractCardDatabaseValuesFromCursor(databaseCursor));

		databaseCursor.close();

		return card;
	}

	private String buildCardByIdSelectionQuery(long id) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.CARD_DECK_ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT));
		queryBuilder.append(String.format("%s, ", DbFieldNames.CARD_BACK_SIDE_TEXT));
		queryBuilder.append(String.format("%s ", DbFieldNames.CARD_ORDER_INDEX));

		queryBuilder.append(String.format("from %s ", DbTableNames.CARDS));

		queryBuilder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return queryBuilder.toString();
	}

	public void deleteCard(Card card) {
		database.beginTransaction();
		try {
			tryDeleteCard(card);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryDeleteCard(Card card) {
		database.delete(DbTableNames.CARDS, String.format("%s = %d", DbFieldNames.ID, card.getId()),
			null);

		if (getCardsCount() == 0) {
			setCurrentCardIndex(INVALID_CURRENT_CARD_INDEX);
		}
		else {
			setCurrentCardIndex(0);
		}

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	public void shuffleCards() {
		database.beginTransaction();
		try {
			tryShuffleCards();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryShuffleCards() {
		List<Integer> currentCardOrderIndexes = getCurrentCardOrderIndexes();
		if (currentCardOrderIndexes.size() <= 1) {
			return;
		}

		List<Integer> newCardOrderIndexes;

		if (currentCardOrderIndexes.size() == 2) {
			newCardOrderIndexes = currentCardOrderIndexes;
			Collections.swap(newCardOrderIndexes, 0, 1);
		}
		else {
			newCardOrderIndexes = new ArrayList<Integer>(currentCardOrderIndexes);
			while (newCardOrderIndexes.equals(currentCardOrderIndexes)) {
				Collections.shuffle(newCardOrderIndexes);
			}
		}

		setCardsOrder(newCardOrderIndexes);

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private List<Integer> getCurrentCardOrderIndexes() {
		Cursor databaseCursor = database.rawQuery(
			buildCardsSelectionQuery(DbFieldNames.CARD_ORDER_INDEX), null);

		List<Integer> cardOrderIndexes = new ArrayList<Integer>();

		while (databaseCursor.moveToNext()) {
			int cardOrderIndex = databaseCursor.getInt(
				databaseCursor.getColumnIndexOrThrow(DbFieldNames.CARD_ORDER_INDEX));
			cardOrderIndexes.add(cardOrderIndex);
		}

		databaseCursor.close();

		return cardOrderIndexes;
	}

	private void setCardsOrder(List<Integer> cardsOrderIndexes) {
		Cursor databaseCursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.ID), null);

		if (databaseCursor.getCount() != cardsOrderIndexes.size()) {
			throw new DbException();
		}

		for (int cardOrderIndex : cardsOrderIndexes) {
			databaseCursor.moveToNext();
			int cardId = databaseCursor.getInt(databaseCursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, cardOrderIndex);
		}

		databaseCursor.close();
	}

	public void resetCardsOrder() {
		database.beginTransaction();
		try {
			tryResetCardsOrder();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryResetCardsOrder() {
		Cursor databaseCursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.ID), null);
		if (databaseCursor.getCount() == 0) {
			return;
		}

		int cardOrderIndex = 0;
		while (databaseCursor.moveToNext()) {
			int cardId = databaseCursor.getInt(databaseCursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, cardOrderIndex);
			cardOrderIndex++;
		}

		databaseCursor.close();

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private void setCardOrderIndex(int cardId, int index) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbFieldNames.CARD_ORDER_INDEX, index);

		database.update(DbTableNames.CARDS, contentValues,
			String.format("%s = %d", DbFieldNames.ID, cardId), null);
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object otherObject) {
		if (this == otherObject) {
			return true;
		}

		if (!(otherObject instanceof Deck)) {
			return false;
		}

		Deck otherDeck = (Deck) otherObject;

		if (id != otherDeck.id) {
			return false;
		}

		if ((title == null) && (otherDeck.title != null)) {
			return false;
		}

		if ((title != null) && !title.equals(otherDeck.title)) {
			return false;
		}

		return true;
	}

	public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>()
	{
		@Override
		public Deck createFromParcel(Parcel parcel) {
			return new Deck(parcel);
		}

		@Override
		public Deck[] newArray(int size) {
			return new Deck[size];
		}
	};

	private Deck(Parcel parcel) {
		database = DbProvider.getInstance().getDatabase();
		decks = DbProvider.getInstance().getDecks();
		lastUpdateDateTimeHandler = DbProvider.getInstance().getLastUpdateTimeHandler();

		readFromParcel(parcel);
	}

	public void readFromParcel(Parcel parcel) {
		id = parcel.readLong();
		title = parcel.readString();
		currentCardIndex = parcel.readInt();
	}

	@Override
	public int describeContents() {
		return SPECIAL_PARCELABLE_OBJECTS_BITMASK;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(title);
		parcel.writeInt(currentCardIndex);
	}
}
