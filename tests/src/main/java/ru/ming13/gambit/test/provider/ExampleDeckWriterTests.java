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


import static org.fest.assertions.api.Assertions.assertThat;

import android.database.Cursor;
import android.net.Uri;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.ExampleDeckWriter;
import ru.ming13.gambit.local.provider.ProviderUris;
import ru.ming13.gambit.local.sqlite.DbFieldNames;


public class ExampleDeckWriterTests extends GambitProviderTestCase
{
	public void testExampleDeckWriting() {
		// Example deck is the only deck in an empty database
		int expectedDecksCount = 1;
		int actualDecksCount = queryDecks().getCount();

		assertThat(actualDecksCount).isEqualTo(expectedDecksCount);
	}

	public void testExampleDeckTitle() {
		String expectedExampleDeckTitlePrefix = getMockContext().getString(R.string.example_deck_title);
		String actualExampleDeckTitle = queryDeckTitle(getExampleDeckUri());

		assertThat(actualExampleDeckTitle).startsWith(expectedExampleDeckTitlePrefix);
	}

	private Uri getExampleDeckUri() {
		Cursor decksCursor = queryDecks();

		decksCursor.moveToFirst();
		long deckId = decksCursor.getLong(decksCursor.getColumnIndex(DbFieldNames.ID));

		return ProviderUris.Content.buildDeckUri(deckId);
	}

	public void testExampleDeckCardsCount() {
		Cursor cardsCursor = queryCards(getExampleDeckUri());

		int expectedCardsCount = ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES.length;
		int actualCardsCount = cardsCursor.getCount();

		assertThat(actualCardsCount).isEqualTo(expectedCardsCount);
	}

	public void testExampleDeckCardsTexts() {
		Cursor cardsCursor = queryCards(getExampleDeckUri());

		cardsCursor.moveToFirst();
		for (int exampleDeckCardResourceId : ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES) {
			String expectedFrontSideText = getMockContext().getString(exampleDeckCardResourceId);
			String actualFrontSideText = cardsCursor.getString(
				cardsCursor.getColumnIndex(DbFieldNames.CARD_FRONT_SIDE_TEXT));

			assertThat(actualFrontSideText).isEqualTo(expectedFrontSideText);

			cardsCursor.moveToNext();
		}
	}
}
