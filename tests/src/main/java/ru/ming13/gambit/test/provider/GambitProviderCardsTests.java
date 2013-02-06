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


import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import android.database.Cursor;
import android.net.Uri;


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

		assertThat(cardsCursor).isNotNull();
		assertThat(cardsCursor).hasCount(0);
	}

	public void testCardsQueryHasValidContents() {
		insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT);

		Cursor cardsCursor = queryCards(deckUri);
		cardsCursor.moveToFirst();

		assertThat(cardsCursor).hasColumnCount(Projection.CARDS.length);
		assertThat(cardsCursor).hasColumns(Projection.CARDS);
	}

	public void testCardInsertion() {
		Uri cardUri = insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT);
		assertThat(cardUri).isNotNull();

		String actualCardFrontSideText = queryCardSidesTexts(cardUri).first;
		String actualCardBackSideText = queryCardSidesTexts(cardUri).second;

		assertThat(actualCardFrontSideText).isEqualTo(Content.CARD_FRONT_SIDE_TEXT);
		assertThat(actualCardBackSideText).isEqualTo(Content.CARD_BACK_SIDE_TEXT);
	}

	public void testCardUpdating() {
		Uri cardUri = insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT);

		String modifiedCardFrontSideText = reverseText(Content.CARD_FRONT_SIDE_TEXT);
		String modifiedCardBackSideText = reverseText(Content.CARD_BACK_SIDE_TEXT);
		updateCard(cardUri, modifiedCardFrontSideText, modifiedCardBackSideText);

		String actualCardFrontSideText = queryCardSidesTexts(cardUri).first;
		String actualCardBackSideText = queryCardSidesTexts(cardUri).second;

		assertThat(actualCardFrontSideText).isEqualTo(modifiedCardFrontSideText);
		assertThat(actualCardBackSideText).isEqualTo(modifiedCardBackSideText);
	}

	public void testCardDeletion() {
		int initialCardsCount = queryCards(deckUri).getCount();

		deleteCard(insertCard(deckUri, Content.CARD_FRONT_SIDE_TEXT, Content.CARD_BACK_SIDE_TEXT));

		Cursor cardsCursor = queryCards(deckUri);

		assertThat(cardsCursor).hasCount(initialCardsCount);
	}
}
