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
import android.support.annotation.NonNull;

import ru.ming13.gambit.bus.BusEvent;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckNotSavedEvent;
import ru.ming13.gambit.bus.DeckSavedEvent;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;

public class DeckCreationTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;

	private final Deck deck;

	public static void execute(@NonNull ContentResolver contentResolver, @NonNull Deck deck) {
		new DeckCreationTask(contentResolver, deck).execute();
	}

	private DeckCreationTask(ContentResolver contentResolver, Deck deck) {
		this.contentResolver = contentResolver;

		this.deck = deck;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		long deckId = getDeckId(createDeck());

		if (isDeckCorrect(deckId)) {
			return new DeckSavedEvent(new Deck(deckId, deck.getTitle()));
		} else {
			return new DeckNotSavedEvent();
		}
	}

	private long getDeckId(Uri deckUri) {
		return GambitContract.Decks.getDeckId(deckUri);
	}

	private Uri createDeck() {
		return contentResolver.insert(buildDecksUri(), buildDeckValues());
	}

	private Uri buildDecksUri() {
		return GambitContract.Decks.getDecksUri();
	}

	private ContentValues buildDeckValues() {
		ContentValues deckValues = new ContentValues();

		deckValues.put(GambitContract.Decks.TITLE, deck.getTitle());
		deckValues.put(GambitContract.Decks.CURRENT_CARD_INDEX, GambitContract.Decks.Defaults.CURRENT_CARD_INDEX);

		return deckValues;
	}

	private boolean isDeckCorrect(long deckId) {
		return deckId >= 0;
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
