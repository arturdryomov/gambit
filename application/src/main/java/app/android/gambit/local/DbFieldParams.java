package app.android.gambit.local;


class DbFieldParams
{
	public static final String ID = "integer primary key autoincrement not null unique";
	public static final String DECK_TITLE = "text not null unique";
	public static final String DECK_NEXT_CARD_INDEX = "int not null";

	public static final String CARD_DECK_ID = "integer not null references Decks(_id)";
	public static final String CARD_FRONT_SIDE_TEXT = "text not null";
	public static final String CARD_BACK_SIDE_TEXT = "text not null";

	public static final String CARD_ORDER_INDEX = "int not null";

	public static final String DB_LAST_UPDATE_TIME = "text not null unique";
}
