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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import ru.ming13.gambit.database.DatabaseSchema;

public final class GambitContract
{
	private GambitContract() {
	}

	public static final String AUTHORITY = "ru.ming13.gambit";

	private interface DecksColumns
	{
		String TITLE = DatabaseSchema.DecksColumns.TITLE;
		String CURRENT_CARD_INDEX = DatabaseSchema.DecksColumns.CURRENT_CARD_INDEX;
	}

	public static final class Decks implements BaseColumns, DecksColumns
	{
		private Decks() {
		}

		public static final class Defaults
		{
			private Defaults() {
			}

			public static final int CURRENT_CARD_INDEX = 0;
		}

		public static Uri getDecksUri() {
			return buildContentUri(getPathsBuilder().buildDecksPath());
		}

		public static Uri getDeckUri(long deckId) {
			return buildContentUri(getPathsBuilder().buildDeckPath(String.valueOf(deckId)));
		}

		public static long getDeckId(Uri deckUri) {
			return parseId(deckUri);
		}
	}

	private interface CardsColumns
	{
		String FRONT_SIDE_TEXT = DatabaseSchema.CardsColumns.FRONT_SIDE_TEXT;
		String BACK_SIDE_TEXT = DatabaseSchema.CardsColumns.BACK_SIDE_TEXT;
		String DECK_ID = DatabaseSchema.CardsColumns.DECK_ID;
		String ORDER_INDEX = DatabaseSchema.CardsColumns.ORDER_INDEX;
	}

	public static final class Cards implements BaseColumns, CardsColumns
	{
		private Cards() {
		}

		public static final class Defaults
		{
			private Defaults() {
			}

			public static final int ORDER_INDEX = 0;
		}

		public static Uri getCardsUri(long deckId) {
			return buildContentUri(getPathsBuilder().buildCardsPath(String.valueOf(deckId)));
		}

		public static Uri getCardUri(long deckId, long cardId) {
			return buildContentUri(getPathsBuilder().buildCardPath(String.valueOf(deckId), String.valueOf(cardId)));
		}

		public static long getDeckId(Uri cardsUri) {
			final int deckIdSegmentPosition = 1;

			return parseId(cardsUri, deckIdSegmentPosition);
		}

		public static long getCardId(Uri cardUri) {
			return parseId(cardUri);
		}
	}

	private static Uri buildContentUri() {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build();
	}

	private static Uri buildContentUri(String path) {
		return Uri.withAppendedPath(buildContentUri(), path);
	}

	private static long parseId(Uri uri) {
		return ContentUris.parseId(uri);
	}

	private static long parseId(Uri uri, int segmentPosition) {
		return Long.valueOf(uri.getPathSegments().get(segmentPosition));
	}

	private static GambitPathsBuilder getPathsBuilder() {
		return new GambitPathsBuilder();
	}
}
