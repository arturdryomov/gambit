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
import ru.ming13.gambit.database.DbSchema;


public class GambitContract
{
	public static final String AUTHORITY = "ru.ming13.gambit.provider";

	private static final Uri CONTENT_URI;

	static {
		CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(
			AUTHORITY).build();
	}

	private GambitContract() {
	}

	private interface DecksColumns
	{
		String TITLE = DbSchema.DecksColumns.TITLE;
		String CURRENT_CARD_INDEX = DbSchema.DecksColumns.CURRENT_CARD_INDEX;
	}

	public static final class Decks implements BaseColumns, DecksColumns
	{
		public static final Uri CONTENT_URI;

		public static final int DEFAULT_CURRENT_CARD_INDEX = DbSchema.DecksColumnsDefaultValues.CURRENT_CARD_INDEX;

		static {
			CONTENT_URI = GambitContract.CONTENT_URI.buildUpon().appendPath(
				GambitProviderPaths.DECKS).build();
		}

		public static Uri buildDeckUri(long deckId) {
			return ContentUris.withAppendedId(CONTENT_URI, deckId);
		}

		public static long getDeckId(Uri deckUri) {
			return ContentUris.parseId(deckUri);
		}
	}

	private interface CardsColumns
	{
		String DECK_ID = DbSchema.CardsColumns.DECK_ID;
		String FRONT_SIDE_TEXT = DbSchema.CardsColumns.FRONT_SIDE_TEXT;
		String BACK_SIDE_TEXT = DbSchema.CardsColumns.BACK_SIDE_TEXT;
		String ORDER_INDEX = DbSchema.CardsColumns.ORDER_INDEX;
	}

	public static final class Cards implements BaseColumns, CardsColumns
	{
		public static final Uri CONTENT_URI;

		public static final int DEFAULT_ORDER_INDEX = DbSchema.CardsColumnsDefaultValues.ORDER_INDEX;

		static {
			CONTENT_URI = GambitContract.CONTENT_URI.buildUpon().appendPath(
				GambitProviderPaths.CARDS).build();
		}

		public static Uri buildCardsUri(Uri deckUri) {
			return Uri.withAppendedPath(deckUri, GambitProviderPaths.Segments.CARDS);
		}

		public static Uri buildCardUri(Uri cardsUri, long cardId) {
			return ContentUris.withAppendedId(cardsUri, cardId);
		}

		public static long getDeckId(Uri cardsUri) {
			final int cardsUriDeckIdSegmentIndex = 1;

			String deckId = cardsUri.getPathSegments().get(cardsUriDeckIdSegmentIndex);
			return Long.parseLong(deckId);
		}

		public static long getCardId(Uri cardUri) {
			return ContentUris.parseId(cardUri);
		}
	}
}
