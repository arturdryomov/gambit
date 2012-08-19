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


import ru.ming13.gambit.local.model.Card;


public class CardTests extends DatabaseTestCase
{
	private static final String DECK_TITLE = "deck";
	private static final String CARD_BACK_SIDE_TEXT = "back side text";
	private static final String CARD_FRONT_SIDE_TEXT = "front side text";

	private Card card;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		card = decks.createDeck(DECK_TITLE).createCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);
	}

	public void testGetId() {
		long cardId = card.getId();

		assertTrue(cardId >= 0);
	}

	public void testGetFrontSideText() {
		assertEquals(CARD_FRONT_SIDE_TEXT, card.getFrontSideText());
	}

	public void testGetBackSideText() {
		assertEquals(CARD_BACK_SIDE_TEXT, card.getBackSideText());
	}

	public void testSetFrontSideText() {
		String newFrontSideText = String.format("new %s", CARD_FRONT_SIDE_TEXT);

		card.setFrontSideText(newFrontSideText);

		assertEquals(newFrontSideText, card.getFrontSideText());
	}

	public void testSetBackSideText() {
		String newBackSideText = String.format("new %s", CARD_BACK_SIDE_TEXT);

		card.setBackSideText(newBackSideText);

		assertEquals(newBackSideText, card.getBackSideText());
	}
}
