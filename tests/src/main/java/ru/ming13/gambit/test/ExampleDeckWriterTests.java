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


import ru.ming13.gambit.R;
import ru.ming13.gambit.local.ExampleDeckWriter;
import ru.ming13.gambit.local.model.Deck;


public class ExampleDeckWriterTests extends DatabaseTestCase
{
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		new ExampleDeckWriter(getContext(), decks).writeDeck();
	}

	public void testWritingDeck() {
		assertFalse(decks.getDecksList().isEmpty());

		assertEquals(1, decks.getDecksList().size());
	}

	public void testDeckTitle() {
		String expectedDeckTitlePrefix = getContext().getString(R.string.example_deck_title);
		String actualDeckTitle = decks.getDecksList().get(0).getTitle();

		assertTrue(actualDeckTitle.startsWith(expectedDeckTitlePrefix));
	}

	public void testDeckCards() {
		Deck deck = decks.getDecksList().get(0);

		int cardsListSize = deck.getCardsList().size();
		assertEquals(ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES.length, cardsListSize);

		for (int cardIndex = 0; cardIndex < cardsListSize; cardIndex++) {
			assertTrue(isCardCorrect(deck, cardIndex));
		}
	}

	private boolean isCardCorrect(Deck deck, int cardIndex) {
		String expectedFrontSideText = getContext().getString(
			ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES[cardIndex]);
		String actualFrontSideText = deck.getCardsList().get(cardIndex).getFrontSideText();

		return expectedFrontSideText.equals(actualFrontSideText);
	}
}
