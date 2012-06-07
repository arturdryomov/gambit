package app.android.gambit.local;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Card implements Parcelable
{
	private static final int SPECIAL_PARCELABLE_OBJECTS_BITMASK = 0;

	private final SQLiteDatabase database;
	private final LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	private long id;
	private String frontSideText;
	private String backSideText;

	Card(ContentValues databaseValues) {
		database = DbProvider.getInstance().getDatabase();
		lastUpdateDateTimeHandler = DbProvider.getInstance().getLastUpdateTimeHandler();

		setValues(databaseValues);
	}

	private void setValues(ContentValues databaseValues) {
		Long idAsLong = databaseValues.getAsLong(DbFieldNames.ID);
		if (idAsLong == null) {
			throw new DbException();
		}
		id = idAsLong.longValue();

		String frontSideAsString = databaseValues.getAsString(DbFieldNames.CARD_FRONT_SIDE_TEXT);
		if (frontSideAsString == null) {
			throw new DbException();
		}
		frontSideText = frontSideAsString;

		String backSideAsString = databaseValues.getAsString(DbFieldNames.CARD_BACK_SIDE_TEXT);
		if (backSideAsString == null) {
			throw new DbException();
		}
		backSideText = backSideAsString;
	}

	public String getFrontSideText() {
		return frontSideText;
	}

	public void setFrontSideText(String text) {
		database.beginTransaction();
		try {
			trySetFrontSideText(text);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetFrontSideText(String text) {
		if (text.equals(frontSideText)) {
			return;
		}

		updateFrontSideText(text);
		frontSideText = text;

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private void updateFrontSideText(String text) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, text);

		database.update(DbTableNames.CARDS, databaseValues,
			String.format("%s = %d", DbFieldNames.ID, id), null);
	}

	public String getBackSideText() {
		return backSideText;
	}

	public void setBackSideText(String text) {
		database.beginTransaction();
		try {
			trySetBackSideText(text);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetBackSideText(String text) {
		if (text.equals(backSideText)) {
			return;
		}

		updateBackSideText(text);
		backSideText = text;

		lastUpdateDateTimeHandler.setCurrentDateTimeAsLastUpdated();
	}

	private void updateBackSideText(String text) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.CARD_BACK_SIDE_TEXT, text);

		database.update(DbTableNames.CARDS, databaseValues,
			String.format("%s = %d", DbFieldNames.ID, id), null);
	}

	public long getId() {
		return id;
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

		if (!(otherObject instanceof Card)) {
			return false;
		}

		Card otherCard = (Card) otherObject;

		if (id != otherCard.id) {
			return false;
		}

		if ((frontSideText == null) && (otherCard.frontSideText != null)) {
			return false;
		}

		if ((frontSideText != null) && !frontSideText.equals(otherCard.frontSideText)) {
			return false;
		}

		if ((backSideText == null) && (otherCard.backSideText != null)) {
			return false;
		}

		if ((backSideText != null) && !backSideText.equals(otherCard.backSideText)) {
			return false;
		}

		return true;
	}

	public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>()
	{
		@Override
		public Card createFromParcel(Parcel parcel) {
			return new Card(parcel);
		}

		@Override
		public Card[] newArray(int size) {
			return new Card[size];
		}
	};

	private Card(Parcel parcel) {
		database = DbProvider.getInstance().getDatabase();
		lastUpdateDateTimeHandler = DbProvider.getInstance().getLastUpdateTimeHandler();

		readFromParcel(parcel);
	}

	public void readFromParcel(Parcel parcel) {
		id = parcel.readLong();
		frontSideText = parcel.readString();
		backSideText = parcel.readString();
	}

	@Override
	public int describeContents() {
		return SPECIAL_PARCELABLE_OBJECTS_BITMASK;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(frontSideText);
		parcel.writeString(backSideText);
	}
}
