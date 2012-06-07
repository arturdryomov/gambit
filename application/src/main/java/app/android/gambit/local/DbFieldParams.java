package app.android.gambit.local;


class DbFieldParams
{
	public static final String ID = "integer primary key autoincrement not null unique";

	public static final String INDEX = "int not null";

	public static final String DECK_TITLE = "text not null unique";
	public static final String DECK_FOREIGN_ID;

	static {
		final String foreignKeyMask = "integer not null references %s(%s)";

		DECK_FOREIGN_ID = String.format(foreignKeyMask, DbTableNames.DECKS, DbFieldNames.ID);
	}

	public static final String CARD_TEXT = "text not null";

	public static final String DB_LAST_UPDATE_TIME = "text not null unique";
}
