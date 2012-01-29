package app.android.simpleflashcards.models;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SimpleFlashcardsOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "SimpleFlashcards";

	public SimpleFlashcardsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(buildCreateDecksQuery());
		db.execSQL(buildCreateCardsQuery());
		db.execSQL(buildCreateNextCardIndexQuery());
		db.execSQL(initNextCardIndexQuery());
	}

	private String buildCreateDecksQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbConstants.TABLE_DECKS));

		builder.append("(");

		builder.append(String.format(" %s %s, ", DbConstants.FIELD_ID, DbConstants.FIELD_PARAM_ID));
		builder.append(String.format(" %s %s ", DbConstants.FIELD_DECK_TITLE,
			DbConstants.FIELD_PARAM_DECK_TITLE));

		builder.append(")");

		return builder.toString();
	}

	private String buildCreateCardsQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbConstants.TABLE_CARDS));

		builder.append("(");

		builder.append(String.format(" %s %s, ", DbConstants.FIELD_ID, DbConstants.FIELD_PARAM_ID));
		builder.append(String.format(" %s %s, ", DbConstants.FIELD_CARD_DECK_ID,
			DbConstants.FIELD_PARAM_CARD_DECK_ID));
		builder.append(String.format(" %s %s, ", DbConstants.FIELD_CARD_FRONT_SIDE_TEXT,
			DbConstants.FIELD_PARAM_CARD_FRONT_SIDE_TEXT));
		builder.append(String.format(" %s %s, ", DbConstants.FIELD_CARD_BACK_SIDE_TEXT,
			DbConstants.FIELD_PARAM_CARD_BACK_SIDE_TEXT));
		builder.append(String.format(" %s %s ", DbConstants.FIELD_CARD_ORDER_INDEX,
			DbConstants.FIELD_PARAM_CARD_ORDER_INDEX));

		builder.append(")");

		return builder.toString();
	}

	private String buildCreateNextCardIndexQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbConstants.TABLE_NEXT_CARD_INDEX));

		builder.append("(");

		builder.append(String.format(" %s %s, ", DbConstants.FIELD_ID, DbConstants.FIELD_PARAM_ID));
		builder.append(String.format(" %s %s ", DbConstants.FIELD_NEXT_CARD_INDEX,
			DbConstants.FIELD_PARAM_NEXT_CARD_INDEX));

		builder.append(")");

		return builder.toString();
	}

	private String initNextCardIndexQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Insert into %s ", DbConstants.TABLE_NEXT_CARD_INDEX));

		builder.append(String.format("(%s) ", DbConstants.FIELD_NEXT_CARD_INDEX));
		builder.append(String.format("values (%d) ", DbConstants.INVALID_NEXT_CARD_INDEX_VALUE));

		return builder.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
