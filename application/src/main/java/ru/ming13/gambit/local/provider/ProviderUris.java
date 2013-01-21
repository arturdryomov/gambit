package ru.ming13.gambit.local.provider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;


public class ProviderUris
{
	private static final String CONTENT = ContentResolver.SCHEME_CONTENT;
	private static final String AUTHORITY = "ru.ming13.gambit.provider";

	private static final Uri BASE;

	public static final UriMatcher MATCHER;

	static {
		BASE = new Uri.Builder().scheme(CONTENT).authority(AUTHORITY).build();

		MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

		MATCHER.addURI(AUTHORITY, Paths.DECKS, Codes.DECKS);
		MATCHER.addURI(AUTHORITY, Paths.DECK, Codes.DECK);
		MATCHER.addURI(AUTHORITY, Paths.CARDS, Codes.CARDS);
		MATCHER.addURI(AUTHORITY, Paths.CARD, Codes.CARD);
	}

	private static final class Paths
	{
		private Paths() {
		}

		public static final String DECKS;
		public static final String DECK;
		public static final String CARDS;
		public static final String CARD;

		static {
			DECKS = PathSegments.DECKS;
			DECK = appendUriPath(DECKS, PathSegments.NUMBER);
			CARDS = appendUriPath(DECK, PathSegments.CARDS);
			CARD = appendUriPath(CARDS, PathSegments.NUMBER);
		}

		private static String appendUriPath(String uri, String uriPath) {
			return String.format("%s/%s", uri, uriPath);
		}
	}

	private static final class PathSegments
	{
		private PathSegments() {
		}

		public static final String NUMBER = "#";

		public static final String DECKS = "decks";
		public static final String CARDS = "cards";
	}

	public static final class Codes
	{
		private Codes() {
		}

		public static final int DECKS = 1;
		public static final int DECK = 2;
		public static final int CARDS = 3;
		public static final int CARD = 4;
	}

	public static final class Content
	{
		private Content() {
		}

		private static final int CARDS_URI_DECK_ID_SEGMENT_INDEX = 1;

		public static Uri buildDecksUri() {
			return Uri.withAppendedPath(BASE, Paths.DECKS);
		}

		public static Uri buildDeckUri(long deckId) {
			return ContentUris.withAppendedId(buildDecksUri(), deckId);
		}

		public static Uri buildCardsUri(Uri deckUri) {
			return Uri.withAppendedPath(deckUri, PathSegments.CARDS);
		}

		public static Uri buildCardUri(Uri cardsUri, long cardId) {
			return ContentUris.withAppendedId(cardsUri, cardId);
		}

		public static long parseDeckId(Uri cardsUri) {
			String deckId = cardsUri.getPathSegments().get(CARDS_URI_DECK_ID_SEGMENT_INDEX);

			return Long.parseLong(deckId);
		}
	}
}
