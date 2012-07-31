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


import java.util.List;

import ru.ming13.gambit.local.Deck;


public class DecksTests extends DatabaseTestCase
{
	private static final String DECK_TITLE = "deck";

	public void testGetDecksList() {
		fillDatabaseWithEmptyDecks();

		List<Deck> decksList = decks.getDecksList();

		assertNotNull(decksList);
	}

	public void testGetDecksCount() {
		decks.createDeck(DECK_TITLE);

		assertEquals(1, decks.getDecksList().size());
	}

	public void testAddDeck() {
		Deck deck = decks.createDeck(DECK_TITLE);

		assertEquals(1, decks.getDecksList().size());
		assertEquals(DECK_TITLE, deck.getTitle());
	}

	public void testDeleteDeck() {
		Deck deck = decks.createDeck(DECK_TITLE);

		decks.deleteDeck(deck);

		assertEquals(0, decks.getDecksList().size());
	}

	public void testClearDecks() {
		fillDatabaseWithEmptyDecks();

		decks.clear();

		assertEquals(0, decks.getDecksList().size());
	}
}
