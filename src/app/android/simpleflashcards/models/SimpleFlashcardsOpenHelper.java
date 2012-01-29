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
		db.execSQL(buildDecksTableCreationQuery());
		db.execSQL(buildCardsTableCreationQuery());
	}

	private String buildDecksTableCreationQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbTableNames.DECKS));

		builder.append("(");
		builder.append(String.format(" %s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		builder.append(String.format(" %s %s ", DbFieldNames.DECK_TITLE, DbFieldParams.DECK_TITLE));
		builder.append(")");

		return builder.toString();
	}

	private String buildCardsTableCreationQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbTableNames.CARDS));

		builder.append("(");

		builder.append(String.format(" %s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		builder.append(String.format(" %s %s, ", DbFieldNames.CARD_DECK_ID,
			DbFieldParams.CARD_DECK_ID));
		builder.append(String.format(" %s %s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT,
			DbFieldParams.CARD_FRONT_SIDE_TEXT));
		builder.append(String.format(" %s %s, ", DbFieldNames.CARD_BACK_SIDE_TEXT,
			DbFieldParams.CARD_BACK_SIDE_TEXT));
		builder.append(String.format(" %s %s ", DbFieldNames.CARD_ORDER_INDEX,
			DbFieldParams.CARD_ORDER_INDEX));

		builder.append(")");

		return builder.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
