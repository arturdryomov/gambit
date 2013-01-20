package ru.ming13.gambit.local.provider;


import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;


public class ProviderUris
{
	private static final String CONTENT = ContentResolver.SCHEME_CONTENT;
	private static final String AUTHORITY = "ru.ming13.gambit.provider";

	private static final Uri BASE;

	public static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	private static final class Paths
	{
		private Paths() {
		}

		public static final String NUMBER = "#";

		public static final String DECKS = "decks";
		public static final String DECK = "decks/#";
	}

	public static final class Codes
	{
		private Codes() {
		}

		public static final int DECKS = 1;
		public static final int DECK = 2;
	}

	public static final class Content
	{
		private Content() {
		}

		public static final Uri DECKS = Uri.withAppendedPath(BASE, Paths.DECKS);
		public static final Uri DECK = Uri.withAppendedPath(DECKS, Paths.NUMBER);
	}

	static {
		BASE = new Uri.Builder().scheme(CONTENT).authority(AUTHORITY).build();

		MATCHER.addURI(AUTHORITY, Paths.DECKS, Codes.DECKS);
		MATCHER.addURI(AUTHORITY, Paths.DECK, Codes.DECK);
	}
}
