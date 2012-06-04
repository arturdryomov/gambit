package app.android.gambit.local;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Deck implements Parcelable
{
	public static final int INVALID_CURRENT_CARD_INDEX = -1;

	private final SQLiteDatabase database;
	private final Decks decks;
	private final LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	private long id;
	private String title;
	private int currentCardIndex;

	Deck(ContentValues values) {
		database = DbProvider.getInstance().getDatabase();
		decks = DbProvider.getInstance().getDecks();
		lastUpdateDateTimeHandler = DbProvider.getInstance().getLastUpdateTimeHandler();

		setValues(values);
	}

	private void setValues(ContentValues values) {
		Long idAsLong = values.getAsLong(DbFieldNames.ID);
		if (idAsLong == null) {
			throw new DbException();
		}
		id = idAsLong.longValue();

		String titleAsString = values.getAsString(DbFieldNames.DECK_TITLE);
		if (titleAsString == null) {
			throw new DbException();
		}
		title = titleAsString;

		Integer currentCardIndexAsInteger = values.getAsInteger(DbFieldNames.DECK_CURRENT_CARD_INDEX);
		if (currentCardIndexAsInteger == null) {
			throw new DbException();
		}
		currentCardIndex = currentCardIndexAsInteger;
	}

	public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>() {
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
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(title);
		parcel.writeInt(currentCardIndex);
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
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbFieldNames.DECK_TITLE, title);

		database.update(DbTableNames.DECKS, contentValues,
			String.format("%s = %d", DbFieldNames.ID, id), null);
	}

	public long getId() {
		return id;
	}

	public boolean isEmpty() {
		return getCardsCount() == 0;
	}

	public int getCardsCount() {
		Cursor cursor = database.rawQuery(buildCardsCountSelectionQuery(), null);
		cursor.moveToFirst();
		int cardsCount = cursor.getInt(0);
		cursor.close();

		return cardsCount;
	}

	private String buildCardsCountSelectionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select count(*) from %s ", DbTableNames.CARDS));
		builder.append(String.format("where %s = %d ", DbFieldNames.CARD_DECK_ID, id));

		return builder.toString();
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
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, index);

		database.update(DbTableNames.DECKS, contentValues,
			String.format("%s = %d", DbFieldNames.ID, id), null);
	}

	public List<Card> getCardsList() {
		List<Card> cardsList = new ArrayList<Card>();

		Cursor cursor = database
			.rawQuery(buildCardsSelectionQuery(DbFieldNames.CARD_ORDER_INDEX), null);

		while (cursor.moveToNext()) {
			ContentValues values = contentValuesFromCursor(cursor);
			cardsList.add(new Card(values));
		}

		cursor.close();

		return cardsList;
	}

	private String buildCardsSelectionQuery(String orderByField) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_DECK_ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbFieldNames.CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbFieldNames.CARD_ORDER_INDEX));

		builder.append(String.format("from %s ", DbTableNames.CARDS));

		builder.append("where ");

		builder.append(String.format("%s = %d ", DbFieldNames.CARD_DECK_ID, id));

		builder.append(String.format("order by %s", orderByField));

		return builder.toString();
	}

	private ContentValues contentValuesFromCursor(Cursor cursor) {
		ContentValues values = new ContentValues(cursor.getCount());

		int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
		values.put(DbFieldNames.ID, id);

		int deckId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.CARD_DECK_ID));
		values.put(DbFieldNames.CARD_DECK_ID, deckId);

		String frontSideText = cursor.getString(cursor
			.getColumnIndexOrThrow(DbFieldNames.CARD_FRONT_SIDE_TEXT));
		values.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, frontSideText);

		String backSideText = cursor.getString(cursor
			.getColumnIndexOrThrow(DbFieldNames.CARD_BACK_SIDE_TEXT));
		values.put(DbFieldNames.CARD_BACK_SIDE_TEXT, backSideText);

		int orderIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.CARD_ORDER_INDEX));
		values.put(DbFieldNames.CARD_ORDER_INDEX, orderIndex);

		return values;
	}

	public Card addNewCard(String frontSideText, String backSideText) {
		database.beginTransaction();
		try {
			Card newCard = tryAddNewCard(frontSideText, backSideText);
			database.setTransactionSuccessful();
			return newCard;
		}
		finally {
			database.endTransaction();
		}
	}

	private Card tryAddNewCard(String frontSideText, String backSideText) {
		Card insertedCard = getCardById(insertCard(frontSideText, backSideText));
		setCurrentCardIndex(0);

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();

		return insertedCard;
	}

	private long insertCard(String frontSideText, String backSideText) {
		// Append to the end
		int newCardOrderIndex = getCardsCount();

		ContentValues contentValues = new ContentValues();

		contentValues.put(DbFieldNames.CARD_DECK_ID, id);
		contentValues.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, frontSideText);
		contentValues.put(DbFieldNames.CARD_BACK_SIDE_TEXT, backSideText);
		contentValues.put(DbFieldNames.CARD_ORDER_INDEX, newCardOrderIndex);

		return database.insert(DbTableNames.CARDS, null, contentValues);
	}

	/**
	 * @throws DbException if there is no card with id specified.
	 */
	public Card getCardById(long id) {
		Cursor cursor = database.rawQuery(buildCardByIdSelectionQuery(id), null);
		if (!cursor.moveToFirst()) {
			throw new DbException(String.format("There's no a card with id = %d in database", id));
		}

		Card card = new Card(contentValuesFromCursor(cursor));

		cursor.close();

		return card;
	}

	private String buildCardByIdSelectionQuery(long id) {
		StringBuilder builder = new StringBuilder();

		builder.append("select ");

		builder.append(String.format("%s, ", DbFieldNames.ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_DECK_ID));
		builder.append(String.format("%s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT));
		builder.append(String.format("%s, ", DbFieldNames.CARD_BACK_SIDE_TEXT));
		builder.append(String.format("%s ", DbFieldNames.CARD_ORDER_INDEX));

		builder.append(String.format("from %s ", DbTableNames.CARDS));

		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
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
		Cursor cursor = database
			.rawQuery(buildCardsSelectionQuery(DbFieldNames.CARD_ORDER_INDEX), null);

		List<Integer> cardOrderIndexes = new ArrayList<Integer>();

		while (cursor.moveToNext()) {
			int index = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.CARD_ORDER_INDEX));
			cardOrderIndexes.add(index);
		}

		cursor.close();

		return cardOrderIndexes;
	}

	private void setCardsOrder(List<Integer> cardsOrderIndexes) {
		Cursor cursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.ID), null);

		if (cursor.getCount() != cardsOrderIndexes.size()) {
			throw new DbException();
		}

		for (int index : cardsOrderIndexes) {
			cursor.moveToNext();
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, index);
		}

		cursor.close();
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
		Cursor cursor = database.rawQuery(buildCardsSelectionQuery(DbFieldNames.ID), null);
		if (cursor.getCount() == 0) {
			return;
		}

		int index = 0;
		while (cursor.moveToNext()) {
			int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(DbFieldNames.ID));
			setCardOrderIndex(cardId, index);
			index++;
		}

		cursor.close();

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
		// hashCode() is not intended to be used
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
}
