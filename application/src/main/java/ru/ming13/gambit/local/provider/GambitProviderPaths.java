/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.local.provider;


import android.content.UriMatcher;


class GambitProviderPaths
{
	private GambitProviderPaths() {
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

	public static final class Segments
	{
		private Segments() {
		}

		public static final String NUMBER = "#";

		public static final String DECKS = "decks";
		public static final String CARDS = "cards";
	}

	private static String appendUriPath(String uri, String uriPath) {
		return String.format("%s/%s", uri, uriPath);
	}

	public static UriMatcher buildUriMatcher() {
		UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(GambitContract.AUTHORITY, DECKS, Codes.DECKS);
		uriMatcher.addURI(GambitContract.AUTHORITY, DECK, Codes.DECK);
		uriMatcher.addURI(GambitContract.AUTHORITY, CARDS, Codes.CARDS);
		uriMatcher.addURI(GambitContract.AUTHORITY, CARD, Codes.CARD);

		return uriMatcher;
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
}
