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


import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import app.android.gambit.local.Decks;


@Suppress
public abstract class DatabaseTestCase extends AndroidTestCase
{
	private static final int DECKS_COUNT = 5;
	private static final int CARDS_COUNT = 5;

	protected Decks decks;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		decks = DbProvider.getInstance(getContext()).getDecks();
		decks.beginTransaction();

		decks.clear();
	}

	@Override
	protected void tearDown() throws Exception {
		decks.endTransaction();

		super.tearDown();
	}

	protected void fillDatabaseWithEmptyDecks() {
		if (decks.getDecksList().size() != 0) {
			return;
		}

		for (int deckIndex = 1; deckIndex <= DECKS_COUNT; deckIndex++) {
			decks.createDeck(String.format("Deck %d", deckIndex));
		}
	}

	protected void fillDeckWithCards(Deck deck) {
		if (deck.getCardsCount() != 0) {
			return;
		}

		for (int cardIndex = 1; cardIndex <= CARDS_COUNT; cardIndex++) {
			String frontSideText = String.format("Card %d in deck %d front", cardIndex, deck.getId());
			String backSideText = String.format("Card %d in deck %d back", cardIndex, deck.getId());

			deck.createCard(frontSideText, backSideText);
		}
	}
}
