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

package ru.ming13.gambit.ui.task;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.AsyncTask;
import ru.ming13.gambit.local.provider.DeckExistsException;
import ru.ming13.gambit.local.provider.ProviderUris;
import ru.ming13.gambit.local.sqlite.DbFieldNames;
import ru.ming13.gambit.ui.bus.BusEvent;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.DeckCreatedEvent;
import ru.ming13.gambit.ui.bus.DeckExistsEvent;


public class DeckCreationTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final String deckTitle;

	public static void execute(ContentResolver contentResolver, String deckTitle) {
		new DeckCreationTask(contentResolver, deckTitle).execute();
	}

	private DeckCreationTask(ContentResolver contentResolver, String deckTitle) {
		this.contentResolver = contentResolver;
		this.deckTitle = deckTitle;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return createDeck();
	}

	private BusEvent createDeck() {
		try {
			contentResolver.insert(ProviderUris.Content.buildDecksUri(), buildDeckValues(deckTitle));

			return new DeckCreatedEvent();
		}
		catch (DeckExistsException e) {
			return new DeckExistsEvent();
		}
	}

	private ContentValues buildDeckValues(String deckTitle) {
		ContentValues deckValues = new ContentValues();

		deckValues.put(DbFieldNames.DECK_TITLE, deckTitle);

		return deckValues;
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getInstance().post(busEvent);
	}
}
