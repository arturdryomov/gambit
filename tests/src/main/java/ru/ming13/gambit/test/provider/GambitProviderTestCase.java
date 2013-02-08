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

package ru.ming13.gambit.test.provider;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.text.TextUtils;
import android.util.Pair;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.provider.GambitProvider;


abstract class GambitProviderTestCase extends ProviderTestCase2<GambitProvider>
{
	protected static final class Projection
	{
		private Projection() {
		}

		public static final String[] DECKS;
		public static final String[] CARDS;

		static {
			DECKS = new String[] {GambitContract.Decks._ID, GambitContract.Decks.TITLE};
			CARDS = new String[] {GambitContract.Cards._ID, GambitContract.Cards.FRONT_SIDE_TEXT, GambitContract.Cards.BACK_SIDE_TEXT};
		}
	}

	protected static final class Content
	{
		private Content() {
		}

		public static final String DECK_TITLE = "deck title";

		public static final String CARD_FRONT_SIDE_TEXT = "card front side text";
		public static final String CARD_BACK_SIDE_TEXT = "card back side text";
	}

	public GambitProviderTestCase() {
		super(GambitProvider.class, GambitContract.AUTHORITY);
	}

	protected String reverseText(String text) {
		return TextUtils.getReverse(text, 0, text.length()).toString();
	}

	protected Cursor queryDecks() {
		Uri decksUri = GambitContract.Decks.CONTENT_URI;

		return getMockContentResolver().query(decksUri, Projection.DECKS, null, null, null);
	}

	protected String queryDeckTitle(Uri deckUri) {
		Cursor deckCursor = getMockContentResolver().query(deckUri, Projection.DECKS, null, null, null);

		deckCursor.moveToFirst();
		return deckCursor.getString(deckCursor.getColumnIndex(GambitContract.Decks.TITLE));
	}

	protected Uri insertDeck(String deckTitle) {
		Uri decksUri = GambitContract.Decks.CONTENT_URI;
		ContentValues deckValues = buildDeckValues(deckTitle);

		return getMockContentResolver().insert(decksUri, deckValues);
	}

	private ContentValues buildDeckValues(String deckTitle) {
		ContentValues deckValues = new ContentValues();

		deckValues.put(GambitContract.Decks.TITLE, deckTitle);
		deckValues.put(GambitContract.Decks.CURRENT_CARD_INDEX,
			GambitContract.Decks.DEFAULT_CURRENT_CARD_INDEX);

		return deckValues;
	}

	protected void updateDeck(Uri deckUri, String deckTitle) {
		ContentValues deckValues = buildDeckValues(deckTitle);

		getMockContentResolver().update(deckUri, deckValues, null, null);
	}

	protected void deleteDeck(Uri deckUri) {
		getMockContentResolver().delete(deckUri, null, null);
	}

	protected Cursor queryCards(Uri deckUri) {
		Uri cardsUri = GambitContract.Cards.buildCardsUri(deckUri);

		return getMockContentResolver().query(cardsUri, Projection.CARDS, null, null, null);
	}

	protected Pair<String, String> queryCardSidesTexts(Uri cardUri) {
		Cursor cardCursor = getMockContentResolver().query(cardUri, Projection.CARDS, null, null, null);

		cardCursor.moveToFirst();
		String cardFrontSideText = cardCursor.getString(
			cardCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
		String cardBackSideText = cardCursor.getString(
			cardCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));
		return Pair.create(cardFrontSideText, cardBackSideText);
	}

	protected Uri insertCard(Uri deckUri, String cardFrontSideText, String cardBackSideText) {
		Uri cardsUri = GambitContract.Cards.buildCardsUri(deckUri);
		ContentValues cardValues = buildCardValues(cardFrontSideText, cardBackSideText);

		return getMockContentResolver().insert(cardsUri, cardValues);
	}

	private ContentValues buildCardValues(String cardFrontSideText, String cardBackSideText) {
		ContentValues cardValues = new ContentValues();

		cardValues.put(GambitContract.Cards.FRONT_SIDE_TEXT, cardFrontSideText);
		cardValues.put(GambitContract.Cards.BACK_SIDE_TEXT, cardBackSideText);
		cardValues.put(GambitContract.Cards.ORDER_INDEX, GambitContract.Cards.DEFAULT_ORDER_INDEX);

		return cardValues;
	}

	protected void updateCard(Uri cardUri, String cardFrontSideText, String cardBackSideText) {
		ContentValues cardValues = buildCardValues(cardFrontSideText, cardBackSideText);

		getMockContentResolver().update(cardUri, cardValues, null, null);
	}

	protected void deleteCard(Uri cardUri) {
		getMockContentResolver().delete(cardUri, null, null);
	}
}
