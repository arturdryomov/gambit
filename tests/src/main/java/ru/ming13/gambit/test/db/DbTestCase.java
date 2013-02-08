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


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ru.ming13.gambit.db.DbOpenHelper;
import ru.ming13.gambit.db.DbSchema;


abstract class DbTestCase extends AndroidTestCase
{
	private static final String DATABASE_FILENAME_PREFIX = "test.";

	protected SQLiteDatabase database;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setContext(new RenamingDelegatingContext(getContext(), DATABASE_FILENAME_PREFIX));

		database = new DbOpenHelper(getContext()).getReadableDatabase();
	}

	protected Cursor queryDecks() {
		return database.query(DbSchema.Tables.DECKS, null, null, null, null, null, null);
	}

	protected Cursor queryCards() {
		return database.query(DbSchema.Tables.CARDS, null, null, null, null, null, null);
	}
}