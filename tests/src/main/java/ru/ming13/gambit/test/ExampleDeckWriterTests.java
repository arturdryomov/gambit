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


import static ru.ming13.gambit.R.string;

import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.local.ExampleDeckWriter;


public class ExampleDeckWriterTests extends DatabaseTestCase
{
	private ExampleDeckWriter exampleDeckWriter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		exampleDeckWriter = new ExampleDeckWriter(getContext(), decks);
	}

	public void testBuildDeck() {
		exampleDeckWriter.writeDeck();

		assertEquals(1, decks.getDecksList().size());

		Deck deck = decks.getDecksList().get(0);
		assertTrue(deck.getTitle().startsWith(getContext().getString(string.example_deck_title)));

		int cardsListSize = deck.getCardsList().size();
		assertEquals(ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES.length, cardsListSize);

		for (int cardIndex = 0; cardIndex < cardsListSize; cardIndex++) {
			assertValidCard(deck, cardIndex);
		}
	}

	private void assertValidCard(Deck deck, int cardIndex) {
		String expectedFrontSideText = getContext().getString(
			ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES[cardIndex]);
		String actualFrontSideText = deck.getCardsList().get(cardIndex).getFrontSideText();

		assertEquals(expectedFrontSideText, actualFrontSideText);
	}
}
