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


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.text.TextUtils;
import ru.ming13.gambit.local.provider.DeckExistsException;
import ru.ming13.gambit.local.provider.GambitProvider;
import ru.ming13.gambit.local.provider.ProviderUris;
import ru.ming13.gambit.local.sqlite.DbFieldNames;


public class GambitProviderDecksTests extends ProviderTestCase2<GambitProvider>
{
	private static final String DECK_TITLE = "deck title";

	public GambitProviderDecksTests() {
		super(GambitProvider.class, "ru.ming13.gambit.provider");
	}

	public void testDecksQuery() {
		Cursor decksCursor = queryDecks();

		assertNotNull(decksCursor);

		// Example deck should be generated
		assertEquals(1, decksCursor.getCount());
	}

	private Cursor queryDecks() {
		String[] projection = {DbFieldNames.ID, DbFieldNames.DECK_TITLE};
		String sort = DbFieldNames.DECK_TITLE;

		return getMockContentResolver().query(ProviderUris.Content.buildDecksUri(), projection, null,
			null, sort);
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
		Uri deckUri = insertDeck(DECK_TITLE);

		assertNotNull(deckUri);
		assertEquals(DECK_TITLE, queryDeckTitle(deckUri));
	}

	private Uri insertDeck(String deckTitle) {
		return getMockContentResolver().insert(ProviderUris.Content.buildDecksUri(),
			buildDeckValues(deckTitle));
	}

	private ContentValues buildDeckValues(String deckTitle) {
		ContentValues deckValues = new ContentValues();

		deckValues.put(DbFieldNames.DECK_TITLE, deckTitle);

		return deckValues;
	}

	private String queryDeckTitle(Uri deckUri) {
		String[] projection = {DbFieldNames.DECK_TITLE};

		Cursor deckCursor = getMockContentResolver().query(deckUri, projection, null, null, null);

		deckCursor.moveToFirst();
		return deckCursor.getString(deckCursor.getColumnIndex(DbFieldNames.DECK_TITLE));
	}

	public void testDuplicateDeckInsertion() {
		try {
			insertDeck(DECK_TITLE);
			insertDeck(DECK_TITLE);

			fail();
		}
		catch (DeckExistsException e) {
		}
	}

	public void testDeckUpdating() {
		Uri deckUri = insertDeck(DECK_TITLE);

		String modifiedDeckTitle = modifyDeckTitle(DECK_TITLE);
		updateDeck(deckUri, modifiedDeckTitle);

		assertEquals(modifiedDeckTitle, queryDeckTitle(deckUri));
	}

	private String modifyDeckTitle(String deckTitle) {
		return TextUtils.getReverse(deckTitle, 0, deckTitle.length()).toString();
	}

	private void updateDeck(Uri deckUri, String deckTitle) {
		getMockContentResolver().update(deckUri, buildDeckValues(deckTitle), null, null);
	}

	public void testDuplicateDeckUpdating() {
		try {
			insertDeck(DECK_TITLE);
			Uri deckUri = insertDeck(modifyDeckTitle(DECK_TITLE));

			updateDeck(deckUri, DECK_TITLE);

			fail();
		}
		catch (DeckExistsException e) {
		}
	}

	public void testDeckDeletion() {
		int initialDecksCount = getDecksCount();

		deleteDeck(insertDeck(DECK_TITLE));

		int finalDecksCount = getDecksCount();

		assertEquals(initialDecksCount, finalDecksCount);
	}

	private int getDecksCount() {
		return queryDecks().getCount();
	}

	private void deleteDeck(Uri deckUri) {
		getMockContentResolver().delete(deckUri, null, null);
	}
}
