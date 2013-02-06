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
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

import android.database.Cursor;
import android.net.Uri;
import ru.ming13.gambit.provider.DeckExistsException;


public class GambitProviderDecksTests extends GambitProviderTestCase
{
	public void testDecksQuerying() {
		Cursor decksCursor = queryDecks();

		assertThat(decksCursor).isNotNull();
	}

	public void testDecksQueryHasValidContents() {
		Cursor decksCursor = queryDecks();
		decksCursor.moveToFirst();

		assertThat(decksCursor).hasColumnCount(Projection.DECKS.length);
		assertThat(decksCursor).hasColumns(Projection.DECKS);
	}

	public void testDeckInsertion() {
		Uri deckUri = insertDeck(Content.DECK_TITLE);
		assertThat(deckUri).isNotNull();

		String actualDeckTitle = queryDeckTitle(deckUri);
		assertThat(actualDeckTitle).isEqualTo(Content.DECK_TITLE);
	}

	public void testDuplicateDeckInsertion() {
		try {
			insertDeck(Content.DECK_TITLE);
			insertDeck(Content.DECK_TITLE);

			failBecauseExceptionWasNotThrown(DeckExistsException.class);
		}
		catch (DeckExistsException e) {
		}
	}

	public void testDeckUpdating() {
		Uri deckUri = insertDeck(Content.DECK_TITLE);

		String modifiedDeckTitle = reverseText(Content.DECK_TITLE);
		updateDeck(deckUri, modifiedDeckTitle);

		String actualDeckTitle = queryDeckTitle(deckUri);

		assertThat(actualDeckTitle).isEqualTo(modifiedDeckTitle);
	}

	public void testDuplicateDeckUpdating() {
		try {
			insertDeck(Content.DECK_TITLE);
			Uri deckUri = insertDeck(reverseText(Content.DECK_TITLE));

			updateDeck(deckUri, Content.DECK_TITLE);

			failBecauseExceptionWasNotThrown(DeckExistsException.class);
		}
		catch (DeckExistsException e) {
		}
	}

	public void testDeckDeletion() {
		int initialDecksCount = queryDecks().getCount();

		deleteDeck(insertDeck(Content.DECK_TITLE));

		Cursor decksCursor = queryDecks();

		assertThat(decksCursor).hasCount(initialDecksCount);
	}
}
