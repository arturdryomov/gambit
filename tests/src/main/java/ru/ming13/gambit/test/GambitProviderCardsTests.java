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

package ru.ming13.gambit.test;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.text.TextUtils;
import android.util.Pair;
import ru.ming13.gambit.local.provider.GambitProvider;
import ru.ming13.gambit.local.provider.ProviderUris;
import ru.ming13.gambit.local.sqlite.DbFieldNames;


public class GambitProviderCardsTests extends ProviderTestCase2<GambitProvider>
{
	private static final String DECK_TITLE = "deck title";
	private static final String CARD_FRONT_SIDE_TEXT = "card front side text";
	private static final String CARD_BACK_SIDE_TEXT = "card back side text";

	private Uri deckUri;

	public GambitProviderCardsTests() {
		super(GambitProvider.class, "ru.ming13.gambit.provider");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deckUri = insertDeck(DECK_TITLE);
	}

	private Uri insertDeck(String deckTitle) {
		return getMockContentResolver().insert(ProviderUris.Content.buildDecksUri(),
			buildDeckValues(deckTitle));
	}

	private ContentValues buildDeckValues(String deckTitle) {
		ContentValues deckValues = new ContentValues();

		deckValues.put(DbFieldNames.DECK_TITLE, deckTitle);

		return deckValues;
	}

	public void testCardsQuerying() {
		Cursor cardsCursor = queryCards();

		assertNotNull(cardsCursor);
		assertEquals(0, cardsCursor.getCount());
	}

	private Cursor queryCards() {
		String[] projection = {DbFieldNames.ID, DbFieldNames.CARD_FRONT_SIDE_TEXT, DbFieldNames.CARD_BACK_SIDE_TEXT};
		String sort = DbFieldNames.CARD_FRONT_SIDE_TEXT;

		return getMockContentResolver().query(ProviderUris.Content.buildCardsUri(deckUri), projection,
			null, null, sort);
	}

	public void testCardsQueryHasValidContents() {
		insertCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);
		Cursor cardsCursor = queryCards();
		cardsCursor.moveToFirst();

		try {
			cardsCursor.getLong(cardsCursor.getColumnIndexOrThrow(DbFieldNames.ID));
			cardsCursor.getString(cardsCursor.getColumnIndexOrThrow(DbFieldNames.CARD_FRONT_SIDE_TEXT));
			cardsCursor.getString(cardsCursor.getColumnIndexOrThrow(DbFieldNames.CARD_BACK_SIDE_TEXT));
		}
		catch (IllegalArgumentException e) {
			fail();
		}
	}

	private Uri insertCard(String cardFrontSideText, String cardBackSideText) {
		return getMockContentResolver().insert(ProviderUris.Content.buildCardsUri(deckUri),
			buildCardValues(cardFrontSideText, cardBackSideText));
	}

	public void testCardInsertion() {
		Uri cardUri = insertCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);

		assertNotNull(cardUri);
		assertEquals(CARD_FRONT_SIDE_TEXT, queryCardSidesTexts(cardUri).first);
		assertEquals(CARD_BACK_SIDE_TEXT, queryCardSidesTexts(cardUri).second);
	}

	private ContentValues buildCardValues(String cardFrontSideText, String cardBackSideText) {
		ContentValues cardValues = new ContentValues();

		cardValues.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, cardFrontSideText);
		cardValues.put(DbFieldNames.CARD_BACK_SIDE_TEXT, cardBackSideText);

		return cardValues;
	}

	private Pair<String, String> queryCardSidesTexts(Uri cardUri) {
		String[] projection = {DbFieldNames.CARD_FRONT_SIDE_TEXT, DbFieldNames.CARD_BACK_SIDE_TEXT};

		Cursor cardCursor = getMockContentResolver().query(cardUri, projection, null, null, null);

		cardCursor.moveToFirst();
		String cardFrontSideText = cardCursor.getString(
			cardCursor.getColumnIndex(DbFieldNames.CARD_FRONT_SIDE_TEXT));
		String cardBackSideText = cardCursor.getString(
			cardCursor.getColumnIndex(DbFieldNames.CARD_BACK_SIDE_TEXT));
		return Pair.create(cardFrontSideText, cardBackSideText);
	}

	public void testCardUpdating() {
		Uri cardUri = insertCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);

		String modifiedCardFrontSideText = modifyCardSideText(CARD_FRONT_SIDE_TEXT);
		String modifiedCardBackSideText = modifyCardSideText(CARD_BACK_SIDE_TEXT);
		updateCard(cardUri, modifiedCardFrontSideText, modifiedCardBackSideText);

		assertEquals(modifiedCardFrontSideText, queryCardSidesTexts(cardUri).first);
		assertEquals(modifiedCardBackSideText, queryCardSidesTexts(cardUri).second);
	}

	private String modifyCardSideText(String cardSideText) {
		return TextUtils.getReverse(cardSideText, 0, cardSideText.length()).toString();
	}

	private void updateCard(Uri cardUri, String cardFrontSideText, String cardBackSideText) {
		getMockContentResolver().update(cardUri, buildCardValues(cardFrontSideText, cardBackSideText),
			null, null);
	}

	public void testCardDeletion() {
		int initialCardsCount = getCardsCount();

		deleteCard(insertCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT));

		int finalCardsCount = getCardsCount();

		assertEquals(initialCardsCount, finalCardsCount);
	}

	private int getCardsCount() {
		return queryCards().getCount();
	}

	private void deleteCard(Uri cardUri) {
		getMockContentResolver().delete(cardUri, null, null);
	}
}
