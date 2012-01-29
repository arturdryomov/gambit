package app.android.simpleflashcards.models;


public class DbConstants
{
	public static final String TABLE_DECKS = "Decks";
	public static final String TABLE_CARDS = "Cards";
	public static final String TABLE_NEXT_CARD_INDEX = "NextCardIndexTable";


	public static final String FIELD_ID = "_id";

	public static final String FIELD_DECK_TITLE = "Title";

	public static final String FIELD_CARD_DECK_ID = "DeckId";
	public static final String FIELD_CARD_FRONT_SIDE_TEXT = "FrontPageSide";
	public static final String FIELD_CARD_BACK_SIDE_TEXT = "BackPageSide";
	public static final String FIELD_CARD_ORDER_INDEX = "OrderIndex";

	public static final String FIELD_NEXT_CARD_INDEX = "NextCardIndex";


	public static final String FIELD_PARAM_ID = "integer primary key autoincrement not null unique";

	public static final String FIELD_PARAM_DECK_TITLE = "text not null unique";

	public static final String FIELD_PARAM_CARD_DECK_ID = "integer not null references \"Decks\"(\"_id\")";
	public static final String FIELD_PARAM_CARD_FRONT_SIDE_TEXT = "text not null";
	public static final String FIELD_PARAM_CARD_BACK_SIDE_TEXT = "text not null";

	// OrderIndex field should actually be unique, but it is hard to shuffle and
	// reset indexes if the db strictly takes care of this. So, uniqueness should be
	// guaranteed by the algorithm.
	public static final String FIELD_PARAM_CARD_ORDER_INDEX = "int not null";

	public static final String FIELD_PARAM_NEXT_CARD_INDEX = "int not null unique";

	public static final int INVALID_NEXT_CARD_INDEX_VALUE = -1;
}
