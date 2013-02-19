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
import ru.ming13.gambit.db.DbSchema;


public class DbCreationTests extends DbTestCase
{
	public void testDecksColumns() {
		Cursor decksCursor = queryDecks();

		String[] decksColumns = {DbSchema.DecksColumns._ID, DbSchema.DecksColumns.TITLE,
			DbSchema.DecksColumns.CURRENT_CARD_INDEX};

		assertThat(decksCursor).hasColumns(decksColumns);
	}

	public void testCardsColumns() {
		Cursor cardsCursor = queryCards();

		String[] cardsColumns = {DbSchema.CardsColumns._ID, DbSchema.CardsColumns.DECK_ID,
			DbSchema.CardsColumns.FRONT_SIDE_TEXT, DbSchema.CardsColumns.BACK_SIDE_TEXT,
			DbSchema.CardsColumns.ORDER_INDEX};

		assertThat(cardsCursor).hasColumns(cardsColumns);
	}
}
