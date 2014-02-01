package ru.ming13.gambit.provider;

import android.content.UriMatcher;

final class GambitUriMatcher
{
	public static final class Codes
	{
		private Codes() {
		}

		public static final int DECKS = 1;
		public static final int DECK = 2;
		public static final int CARDS = 3;
		public static final int CARD = 4;
	}

	private static final class Masks
	{
		private Masks() {
		}

		public static final String NUMBER = "#";
	}

	private final GambitPathsBuilder pathsBuilder;

	public static UriMatcher getMatcher() {
		return new GambitUriMatcher().buildMatcher();
	}

	private GambitUriMatcher() {
		pathsBuilder = new GambitPathsBuilder();
	}

	private UriMatcher buildMatcher() {
		UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(GambitContract.AUTHORITY,
			pathsBuilder.buildDecksPath(), Codes.DECKS);
		uriMatcher.addURI(GambitContract.AUTHORITY,
			pathsBuilder.buildDeckPath(Masks.NUMBER), Codes.DECK);

		uriMatcher.addURI(GambitContract.AUTHORITY,
			pathsBuilder.buildCardsPath(Masks.NUMBER), Codes.CARDS);
		uriMatcher.addURI(GambitContract.AUTHORITY,
			pathsBuilder.buildCardPath(Masks.NUMBER, Masks.NUMBER), Codes.CARD);

		return uriMatcher;
	}
}
