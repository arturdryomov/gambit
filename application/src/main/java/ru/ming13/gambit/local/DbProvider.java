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

package ru.ming13.gambit.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import ru.ming13.gambit.local.model.Decks;
import ru.ming13.gambit.local.sqlite.DbOpenHelper;


public class DbProvider
{
	private static class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private static DbProvider instance;

	private Context context;
	private DbOpenHelper databaseOpenHelper;
	private Decks decks;

	public static DbProvider getInstance() {
		return instance;
	}

	/**
	 * @throws AlreadyInstantiatedException if this method is called more
	 * than once.
	 */
	public static DbProvider getInstance(Context context) {
		if (instance == null) {
			return new DbProvider(context);
		}
		else {
			return instance;
		}
	}

	private DbProvider(Context context) {
		if (instance != null) {
			throw new AlreadyInstantiatedException();
		}

		databaseOpenHelper = new DbOpenHelper(context.getApplicationContext());

		instance = this;

		this.context = context;
	}

	public Decks getDecks() {
		if (decks == null) {
			decks = createDecks();
		}
		return decks;
	}

	private Decks createDecks() {
		Decks decks = new Decks();

		ExampleDeckWriter exampleDeckWriter = new ExampleDeckWriter(context, decks);
		if (exampleDeckWriter.shouldWriteDeck()) {
			exampleDeckWriter.writeDeck();
		}

		return decks;
	}

	public SQLiteDatabase getDatabase() {
		return databaseOpenHelper.getWritableDatabase();
	}
}
