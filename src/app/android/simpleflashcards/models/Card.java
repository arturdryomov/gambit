package app.android.simpleflashcards.models;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class Card
{
	private SQLiteDatabase database;

	private int id;
	private String frontSideText;
	private String backSideText;

	// Do not use the constructor. It should be used by Deck class only
	public Card(SQLiteDatabase database, ContentValues values) {
		this.database = database;

		setValues(values);
	}

	private void setValues(ContentValues values) {
		Integer idAsInteger = values.getAsInteger(DbConstants.FIELD_ID);
		if (idAsInteger == null) {
			throw new ModelsException();
		}
		id = idAsInteger;

		String frontSideAsString = values.getAsString(DbConstants.FIELD_CARD_FRONT_SIDE_TEXT);
		if (frontSideAsString == null) {
			throw new ModelsException();
		}
		frontSideText = frontSideAsString;

		String backSideAsString = values.getAsString(DbConstants.FIELD_CARD_BACK_SIDE_TEXT);
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

		builder.append(String.format("update %s set ", DbConstants.TABLE_CARDS));
		builder.append(String.format("%s = '%s' ", DbConstants.FIELD_CARD_FRONT_SIDE_TEXT, text));
		builder.append(String.format("where %s = %d", DbConstants.FIELD_ID, id));

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

		builder.append(String.format("update %s set ", DbConstants.TABLE_CARDS));
		builder.append(String.format("%s = '%s' ", DbConstants.FIELD_CARD_BACK_SIDE_TEXT, text));
		builder.append(String.format("where %s = %d", DbConstants.FIELD_ID, id));

		return builder.toString();
	}

	public int getId() {
		return id;
	}
}
