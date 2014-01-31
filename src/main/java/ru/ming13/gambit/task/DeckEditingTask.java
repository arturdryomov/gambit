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
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;


public class DeckEditingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final Uri deckUri;
	private final Deck deck;

	public static void execute(ContentResolver contentResolver, Uri deckUri, Deck deck) {
		new DeckEditingTask(contentResolver, deckUri, deck).execute();
	}

	private DeckEditingTask(ContentResolver contentResolver, Uri deckUri, Deck deck) {
		this.contentResolver = contentResolver;
		this.deckUri = deckUri;
		this.deck = deck;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return editDeck();
	}

	private BusEvent editDeck() {
		try {
			contentResolver.update(deckUri, buildDeckValues(), null, null);

			return new DeckSavedEvent();
		} catch (RuntimeException e) {
			return new DeckExistsEvent();
		}
	}

	private ContentValues buildDeckValues() {
		ContentValues deckValues = new ContentValues();

		deckValues.put(GambitContract.Decks.TITLE, deck.getTitle());

		return deckValues;
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
