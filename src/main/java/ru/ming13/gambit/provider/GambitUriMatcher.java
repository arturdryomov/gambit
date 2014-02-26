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
