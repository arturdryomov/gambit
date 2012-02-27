package app.android.simpleflashcards.models;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class Card
{
	private final SQLiteDatabase database;

	private int id;
	private String frontSideText;
	private String backSideText;

	// Do not use the constructor. It should be used by Deck class only
	public Card(SQLiteDatabase database, ContentValues values) {
		this.database = database;

		setValues(values);
	}

	private void setValues(ContentValues values) {
		Integer idAsInteger = values.getAsInteger(DbFieldNames.ID);
		if (idAsInteger == null) {
			throw new ModelsException();
		}
		id = idAsInteger;

		String frontSideAsString = values.getAsString(DbFieldNames.CARD_FRONT_SIDE_TEXT);
		if (frontSideAsString == null) {
			throw new ModelsException();
		}
		frontSideText = frontSideAsString;

		String backSideAsString = values.getAsString(DbFieldNames.CARD_BACK_SIDE_TEXT);
		if (backSideAsString == null) {
			throw new ModelsException();
		}
		backSideText = backSideAsString;
	}

	public String getFrontSideText() {
		return frontSideText;
	}

	public void setFrontSideText(String text) {
		if (text.equals(frontSideText)) {
			return;
		}

		database.execSQL(buildFrontSideTextUpdatingQuery(text));
		frontSideText = text;
	}

	private String buildFrontSideTextUpdatingQuery(String text) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s set ", DbTableNames.CARDS));
		builder.append(String.format("%s = '%s' ", DbFieldNames.CARD_FRONT_SIDE_TEXT, text));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	public String getBackSideText() {
		return backSideText;
	}

	public void setBackSideText(String text) {
		if (text.equals(backSideText)) {
			return;
		}

		database.execSQL(buildBackSideTextUpdatingQuery(text));
		backSideText = text;
	}

	private String buildBackSideTextUpdatingQuery(String text) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s set ", DbTableNames.CARDS));
		builder.append(String.format("%s = '%s' ", DbFieldNames.CARD_BACK_SIDE_TEXT, text));
		builder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return builder.toString();
	}

	public int getId() {
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

		if (frontSideText == null) {
			if (otherCard.frontSideText == null) {
				return false;
			}
		}

		if (!frontSideText.equals(otherCard.frontSideText)) {
			return false;
		}

		if (backSideText == null) {
			if (otherCard.backSideText == null) {
				return false;
			}
		}

		if (!backSideText.equals(otherCard.backSideText)) {
			return false;
		}

		return true;
	}
}
