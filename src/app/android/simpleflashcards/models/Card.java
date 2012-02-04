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
	public Card(ContentValues values) {
		this.database = DatabaseProvider.getInstance().getDatabase();

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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((backSideText == null) ? 0 : backSideText.hashCode());
		result = prime * result + ((frontSideText == null) ? 0 : frontSideText.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Card)) {
			return false;
		}
		Card other = (Card) obj;
		if (backSideText == null) {
			if (other.backSideText != null) {
				return false;
			}
		}
		else if (!backSideText.equals(other.backSideText)) {
			return false;
		}
		if (frontSideText == null) {
			if (other.frontSideText != null) {
				return false;
			}
		}
		else if (!frontSideText.equals(other.frontSideText)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}
}
