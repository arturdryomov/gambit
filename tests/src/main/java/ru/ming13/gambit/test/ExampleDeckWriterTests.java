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


import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.ExampleDeckWriter;
import ru.ming13.gambit.local.provider.GambitProvider;
import ru.ming13.gambit.local.provider.ProviderUris;
import ru.ming13.gambit.local.sqlite.DbFieldNames;


public class ExampleDeckWriterTests extends ProviderTestCase2<GambitProvider>
{
	public ExampleDeckWriterTests() {
		super(GambitProvider.class, "ru.ming13.gambit.provider");
	}

	public void testWritingExampleDeck() {
		// Example deck is the only deck in an empty database
		assertEquals(1, queryDecks().getCount());
	}

	private Cursor queryDecks() {
		String[] projection = {DbFieldNames.ID, DbFieldNames.DECK_TITLE};
		String sort = DbFieldNames.DECK_TITLE;

		return getMockContentResolver().query(ProviderUris.Content.buildDecksUri(), projection, null,
			null, sort);
	}

	public void testExampleDeckTitle() {
		String expectedExampleDeckTitlePrefix = getContext().getString(R.string.example_deck_title);
		String actualExampleDeckTitle = queryDeckTitle(queryExampleDeck());

		assertTrue(actualExampleDeckTitle.startsWith(expectedExampleDeckTitlePrefix));
	}

	private Uri queryExampleDeck() {
		Cursor decksCursor = queryDecks();

		decksCursor.moveToFirst();
		long deckId = decksCursor.getLong(decksCursor.getColumnIndex(DbFieldNames.ID));

		return ProviderUris.Content.buildDeckUri(deckId);
	}

	private String queryDeckTitle(Uri deckUri) {
		String[] projection = {DbFieldNames.DECK_TITLE};

		Cursor deckCursor = getMockContentResolver().query(deckUri, projection, null, null, null);

		deckCursor.moveToFirst();
		return deckCursor.getString(deckCursor.getColumnIndex(DbFieldNames.DECK_TITLE));
	}

	public void testExampleDeckCards() {
		Cursor cardsCursor = queryCards(queryExampleDeck());

		assertEquals(ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES.length, cardsCursor.getCount());

		cardsCursor.moveToFirst();
		for (int exampleDeckCardResourceId : ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES) {
			String expectedFrontSideText = getContext().getString(exampleDeckCardResourceId);
			String actualFrontSideText = cardsCursor.getString(
				cardsCursor.getColumnIndex(DbFieldNames.CARD_FRONT_SIDE_TEXT));

			assertEquals(expectedFrontSideText, actualFrontSideText);

			cardsCursor.moveToNext();
		}
	}

	private Cursor queryCards(Uri deckUri) {
		String[] projection = {DbFieldNames.ID, DbFieldNames.CARD_FRONT_SIDE_TEXT, DbFieldNames.CARD_BACK_SIDE_TEXT};
		String sort = DbFieldNames.CARD_FRONT_SIDE_TEXT;

		return getMockContentResolver().query(ProviderUris.Content.buildCardsUri(deckUri), projection,
			null, null, sort);
	}
}
