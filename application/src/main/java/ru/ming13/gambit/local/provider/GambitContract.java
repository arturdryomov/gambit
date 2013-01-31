package ru.ming13.gambit.local.provider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import ru.ming13.gambit.local.sqlite.DbSchema;


public class GambitContract
{
	public static final String AUTHORITY = "ru.ming13.gambit.provider";

	public static final Uri CONTENT_URI;

	static {
		CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(
			AUTHORITY).build();
	}

	private GambitContract() {
	}

	static final class Paths
	{
		private Paths() {
		}

		public static final class Segments
		{
			private Segments() {
			}

			public static final String NUMBER = "#";

			public static final String DECKS = "decks";
			public static final String CARDS = "cards";
		}

		public static final String DECKS;
		public static final String DECK;
		public static final String CARDS;
		public static final String CARD;

		static {
			DECKS = Segments.DECKS;
			DECK = appendUriPath(DECKS, Segments.NUMBER);
			CARDS = appendUriPath(DECK, Segments.CARDS);
			CARD = appendUriPath(CARDS, Segments.NUMBER);
		}

		private static String appendUriPath(String uri, String uriPath) {
			return String.format("%s/%s", uri, uriPath);
		}
	}

	private interface DecksColumns
	{
		public static final String TITLE = DbSchema.DecksColumns.TITLE;
		public static final String CURRENT_CARD_INDEX = DbSchema.DecksColumns.CURRENT_CARD_INDEX;
	}

	public static final class Decks implements BaseColumns, DecksColumns
	{
		public static final Uri CONTENT_URI;

		public static final String DEFAULT_SORT_ORDER = DecksColumns.TITLE;

		public static final int DEFAULT_CURRENT_CARD_INDEX = 0;

		static {
			CONTENT_URI = GambitContract.CONTENT_URI.buildUpon().appendPath(Paths.DECKS).build();
		}

		public static Uri buildDeckUri(long deckId) {
			return ContentUris.withAppendedId(CONTENT_URI, deckId);
		}
	}

	private interface CardsColumns
	{
		public static final String DECK_ID = DbSchema.CardsColumns.DECK_ID;
		public static final String FRONT_SIDE_TEXT = DbSchema.CardsColumns.FRONT_SIDE_TEXT;
		public static final String BACK_SIDE_TEXT = DbSchema.CardsColumns.BACK_SIDE_TEXT;
		public static final String ORDER_INDEX = DbSchema.CardsColumns.ORDER_INDEX;
	}

	public static final class Cards implements BaseColumns, CardsColumns
	{
		public static final Uri CONTENT_URI;

		public static final String DEFAULT_SORT_ORDER = CardsColumns.FRONT_SIDE_TEXT;

		public static final int DEFAULT_ORDER_INDEX = 0;

		static {
			CONTENT_URI = GambitContract.CONTENT_URI.buildUpon().appendPath(Paths.CARDS).build();
		}

		public static Uri buildCardsUri(Uri deckUri) {
			return Uri.withAppendedPath(deckUri, Paths.Segments.CARDS);
		}

		public static Uri buildCardUri(Uri cardsUri, long cardId) {
			return ContentUris.withAppendedId(cardsUri, cardId);
		}

		public static long getDeckId(Uri cardsUri) {
			final int cardsUriDeckIdSegmentIndex = 1;

			String deckId = cardsUri.getPathSegments().get(cardsUriDeckIdSegmentIndex);
			return Long.parseLong(deckId);
		}
	}
}
