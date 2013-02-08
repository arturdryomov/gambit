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

package ru.ming13.gambit.test.db;


import static org.fest.assertions.api.ANDROID.assertThat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.fest.assertions.api.Assertions;
import ru.ming13.gambit.R;
import ru.ming13.gambit.db.DbOpenHelper;
import ru.ming13.gambit.db.DbSchema;
import ru.ming13.gambit.db.ExampleDeckWriter;
import ru.ming13.gambit.provider.GambitContract;


public class ExampleDeckWriterTests extends DbTestCase
{
	protected SQLiteDatabase database;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		database = new DbOpenHelper(getContext()).getReadableDatabase();
	}

	public void testExampleDeckWriting() {
		// Example deck is the only deck in an empty database
		int expectedDecksCount = 1;
		Cursor decksCursor = queryDecks();

		assertThat(decksCursor).hasCount(expectedDecksCount);
	}

	private Cursor queryDecks() {
		return database.query(DbSchema.Tables.DECKS, null, null, null, null, null, null);
	}

	public void testExampleDeckTitle() {
		String expectedExampleDeckTitlePrefix = getContext().getString(R.string.example_deck_title);
		String actualExampleDeckTitle = queryExampleDeckTitle();

		Assertions.assertThat(actualExampleDeckTitle).startsWith(expectedExampleDeckTitlePrefix);
	}

	private String queryExampleDeckTitle() {
		Cursor decksCursor = queryDecks();

		decksCursor.moveToFirst();
		return decksCursor.getString(decksCursor.getColumnIndex(DbSchema.DecksColumns.TITLE));
	}

	public void testExampleDeckCardsCount() {
		int expectedCardsCount = ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES.length;
		Cursor cardsCursor = queryExampleDeckCards();

		assertThat(cardsCursor).hasCount(expectedCardsCount);
	}

	private Cursor queryExampleDeckCards() {
		long exampleDeckId = queryExampleDeckId();

		String cardsSelectionClause = String.format("%s = %d", DbSchema.CardsColumns.DECK_ID,
			exampleDeckId);

		return database.query(DbSchema.Tables.CARDS, null, cardsSelectionClause, null, null, null,
			null);
	}

	private long queryExampleDeckId() {
		Cursor decksCursor = queryDecks();

		decksCursor.moveToFirst();
		return decksCursor.getLong(decksCursor.getColumnIndex(DbSchema.DecksColumns._ID));
	}

	public void testExampleDeckCardsTexts() {
		Cursor cardsCursor = queryExampleDeckCards();

		cardsCursor.moveToFirst();
		for (int exampleDeckCardResourceId : ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES) {
			String expectedFrontSideText = getContext().getString(exampleDeckCardResourceId);
			String actualFrontSideText = cardsCursor.getString(
				cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));

			Assertions.assertThat(actualFrontSideText).isEqualTo(expectedFrontSideText);

			cardsCursor.moveToNext();
		}
	}
}