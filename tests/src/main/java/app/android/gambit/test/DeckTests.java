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

package app.android.gambit.test;


import java.util.List;

import android.test.suitebuilder.annotation.Suppress;
import app.android.gambit.local.Card;
import app.android.gambit.local.Deck;


public class DeckTests extends DatabaseTestCase
{
	private static final String DECK_TITLE = "deck";
	private static final String CARD_BACK_SIDE_TEXT = "back side text";
	private static final String CARD_FRONT_SIDE_TEXT = "front side text";

	private Deck deck;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deck = decks.createDeck(DECK_TITLE);
	}

	public void testGetId() {
		long deckId = deck.getId();

		assertTrue(deckId >= 0);
	}

	public void testGetTitle() {
		assertEquals(DECK_TITLE, deck.getTitle());
	}

	public void testSetTitle() {
		String newDeckTitle = String.format("new %s", DECK_TITLE);

		deck.setTitle(newDeckTitle);

		assertEquals(newDeckTitle, deck.getTitle());
	}

	public void testCheckEmptiness() {
		assertTrue(deck.isEmpty());
	}

	public void testGetCardsCount() {
		deck.createCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);

		assertEquals(1, deck.getCardsCount());
	}

	public void testGetCardsList() {
		fillDeckWithCards(deck);

		List<Card> cardsList = deck.getCardsList();

		assertNotNull(cardsList);
		assertFalse(cardsList.isEmpty());
	}

	public void testAddCard() {
		Card card = deck.createCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);

		assertEquals(1, deck.getCardsCount());
		assertEquals(CARD_FRONT_SIDE_TEXT, card.getFrontSideText());
		assertEquals(CARD_BACK_SIDE_TEXT, card.getBackSideText());
	}

	public void testDeleteCard() {
		Card card = deck.createCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);

		deck.deleteCard(card);

		assertEquals(0, deck.getCardsCount());
	}

	public void testResetCardsOrder() {
		fillDeckWithCards(deck);

		try {
			deck.resetCardsOrder();
		}
		catch (Exception e) {
			fail();
		}
	}

	@Suppress
	public void testShuffleCardsOrder() {
		fillDeckWithCards(deck);

		try {
			deck.shuffleCards();
		}
		catch (Exception e) {
			fail();
		}
	}

	public void testGetSetCurrentCardIndex() {
		fillDeckWithCards(deck);
		final int currentCardIndex = 1;

		deck.setCurrentCardIndex(currentCardIndex);

		assertEquals(currentCardIndex, deck.getCurrentCardIndex());
	}

	public void testCurrentCardIndexIsZeroAfterInsertion() {
		deck.setCurrentCardIndex(1);

		deck.createCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);

		assertEquals(0, deck.getCurrentCardIndex());
	}

	public void testCurrentCardIndexIsInvalidOnEmptyDeck() {
		fillDeckWithCards(deck);
		deck.setCurrentCardIndex(1);

		for (Card card : deck.getCardsList()) {
			deck.deleteCard(card);
		}

		assertEquals(Deck.INVALID_CURRENT_CARD_INDEX, deck.getCurrentCardIndex());
	}
}
