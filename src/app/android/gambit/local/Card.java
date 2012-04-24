package app.android.gambit.local;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Card implements Parcelable
{
	private final SQLiteDatabase database;
	private final LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	private long id;
	private String frontSideText;
	private String backSideText;

	Card(ContentValues values) {
		database = DatabaseProvider.getInstance().getDatabase();
		lastUpdateDateTimeHandler = DatabaseProvider.getInstance().getLastUpdateTimeHandler();

		setValues(values);
	}

	private void setValues(ContentValues values) {
		Integer idAsInteger = values.getAsInteger(DbFieldNames.ID);
		if (idAsInteger == null) {
			throw new DatabaseException();
		}
		id = idAsInteger;

		String frontSideAsString = values.getAsString(DbFieldNames.CARD_FRONT_SIDE_TEXT);
		if (frontSideAsString == null) {
			throw new DatabaseException();
		}
		frontSideText = frontSideAsString;

		String backSideAsString = values.getAsString(DbFieldNames.CARD_BACK_SIDE_TEXT);
		if (backSideAsString == null) {
			throw new DatabaseException();
		}
		backSideText = backSideAsString;
	}

	public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
		public Card createFromParcel(Parcel parcel) {
			return new Card(parcel);
		}

		public Card[] newArray(int size) {
			return new Card[size];
		}
	};

	private Card(Parcel parcel) {
		database = DatabaseProvider.getInstance().getDatabase();
		lastUpdateDateTimeHandler = DatabaseProvider.getInstance().getLastUpdateTimeHandler();

		readFromParcel(parcel);
	}

	public void readFromParcel(Parcel parcel) {
		id = parcel.readInt();
		frontSideText = parcel.readString();
		backSideText = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(frontSideText);
		parcel.writeString(backSideText);
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
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, text);

		database.update(DbTableNames.CARDS, contentValues,
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
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbFieldNames.CARD_BACK_SIDE_TEXT, text);

		database.update(DbTableNames.CARDS, contentValues,
			String.format("%s = %d", DbFieldNames.ID, id), null);
	}

	public long getId() {
		return id;
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
}
