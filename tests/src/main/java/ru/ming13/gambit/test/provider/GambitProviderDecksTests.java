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
import ru.ming13.gambit.local.provider.DeckExistsException;
import ru.ming13.gambit.local.sqlite.DbFieldNames;


public class GambitProviderDecksTests extends GambitProviderTestCase
{
	public void testDecksQuerying() {
		Cursor decksCursor = queryDecks();

		assertNotNull(decksCursor);
	}

	public void testDecksQueryHasValidContents() {
		Cursor decksCursor = queryDecks();
		decksCursor.moveToFirst();

		try {
			decksCursor.getLong(decksCursor.getColumnIndexOrThrow(DbFieldNames.ID));
			decksCursor.getString(decksCursor.getColumnIndexOrThrow(DbFieldNames.DECK_TITLE));
		}
		catch (IllegalArgumentException e) {
			fail();
		}
	}

	public void testDeckInsertion() {
		Uri deckUri = insertDeck(Content.DECK_TITLE);

		assertNotNull(deckUri);
		assertEquals(Content.DECK_TITLE, queryDeckTitle(deckUri));
	}

	public void testDuplicateDeckInsertion() {
		try {
			insertDeck(Content.DECK_TITLE);
			insertDeck(Content.DECK_TITLE);

			fail();
		}
		catch (DeckExistsException e) {
		}
	}

	public void testDeckUpdating() {
		Uri deckUri = insertDeck(Content.DECK_TITLE);

		String modifiedDeckTitle = reverseText(Content.DECK_TITLE);
		updateDeck(deckUri, modifiedDeckTitle);

		assertEquals(modifiedDeckTitle, queryDeckTitle(deckUri));
	}

	public void testDuplicateDeckUpdating() {
		try {
			insertDeck(Content.DECK_TITLE);
			Uri deckUri = insertDeck(reverseText(Content.DECK_TITLE));

			updateDeck(deckUri, Content.DECK_TITLE);

			fail();
		}
		catch (DeckExistsException e) {
		}
	}

	public void testDeckDeletion() {
		int initialDecksCount = queryDecks().getCount();

		deleteDeck(insertDeck(Content.DECK_TITLE));

		int finalDecksCount = queryDecks().getCount();

		assertEquals(initialDecksCount, finalDecksCount);
	}
}
