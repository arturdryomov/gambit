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


import android.database.Cursor;
import android.net.Uri;
import ru.ming13.gambit.local.sqlite.DbFieldNames;


public class GambitProviderCardsTests extends GambitProviderTestCase
{
	private Uri deckUri;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deckUri = insertDeck(Content.DECK_TITLE);
	}

	public void testCardsQuerying() {
		Cursor cardsCursor = queryCards(deckUri);

		assertNotNull(cardsCursor);
		assertEquals(0, cardsCursor.getCount());
	}

	public void testCardsQueryHasValidContents() {
		insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT);
		Cursor cardsCursor = queryCards(deckUri);
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

	public void testCardInsertion() {
		Uri cardUri = insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT);

		assertNotNull(cardUri);
		assertEquals(Content.CARD_FRONT_SIDE_TEXT, queryCardSidesTexts(cardUri).first);
		assertEquals(Content.CARD_BACK_SIDE_TEXT, queryCardSidesTexts(cardUri).second);
	}

	public void testCardUpdating() {
		Uri cardUri = insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT);

		String modifiedCardFrontSideText = reverseText(Content.CARD_FRONT_SIDE_TEXT);
		String modifiedCardBackSideText = reverseText(Content.CARD_BACK_SIDE_TEXT);
		updateCard(cardUri, modifiedCardFrontSideText, modifiedCardBackSideText);

		assertEquals(modifiedCardFrontSideText, queryCardSidesTexts(cardUri).first);
		assertEquals(modifiedCardBackSideText, queryCardSidesTexts(cardUri).second);
	}

	public void testCardDeletion() {
		int initialCardsCount = queryCards(deckUri).getCount();

		deleteCard(insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT));

		int finalCardsCount = queryCards(deckUri).getCount();

		assertEquals(initialCardsCount, finalCardsCount);
	}
}
