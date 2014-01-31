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

package ru.ming13.gambit.task;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.gambit.bus.BusEvent;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckExistsEvent;
import ru.ming13.gambit.bus.DeckSavedEvent;
import ru.ming13.gambit.provider.GambitContract;


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
		if (isDeckCorrect(createDeck())) {
			return new DeckSavedEvent();
		} else {
			return new DeckExistsEvent();
		}
	}

	private Uri createDeck() {
		Uri decksUri = buildDecksUri();
		ContentValues deckValues = buildDeckValues(deckTitle);

		return contentResolver.insert(decksUri, deckValues);
	}

	private Uri buildDecksUri() {
		return GambitContract.Decks.getDecksUri();
	}

	private ContentValues buildDeckValues(String deckTitle) {
		ContentValues deckValues = new ContentValues();

		deckValues.put(GambitContract.Decks.TITLE, deckTitle);
		deckValues.put(GambitContract.Decks.CURRENT_CARD_INDEX, GambitContract.Decks.Defaults.CURRENT_CARD_INDEX);

		return deckValues;
	}

	private boolean isDeckCorrect(Uri deckUri) {
		return GambitContract.Decks.getDeckId(deckUri) >= 0;
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
