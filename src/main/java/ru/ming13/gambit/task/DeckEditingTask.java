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

public class DeckEditingTask extends AsyncTask<Void, Void, BusEvent>
{
	private static enum SilentMode
	{
		ENABLED, DISABLED
	}

	private final ContentResolver contentResolver;

	private final Deck deck;
	private final SilentMode silentMode;

	public static void execute(@NonNull ContentResolver contentResolver, @NonNull Deck deck) {
		new DeckEditingTask(contentResolver, deck, SilentMode.DISABLED).execute();
	}

	public static void executeSilently(@NonNull ContentResolver contentResolver, @NonNull Deck deck) {
		new DeckEditingTask(contentResolver, deck, SilentMode.ENABLED).execute();
	}

	private DeckEditingTask(ContentResolver contentResolver, Deck deck, SilentMode silentMode) {
		this.contentResolver = contentResolver;

		this.deck = deck;
		this.silentMode = silentMode;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		try {
			editDeck();

			return new DeckSavedEvent(deck);
		} catch (RuntimeException e) {
			return new DeckNotSavedEvent();
		}
	}

	private void editDeck() {
		contentResolver.update(buildDeckUri(), buildDeckValues(), null, null);
	}

	private Uri buildDeckUri() {
		return GambitContract.Decks.getDeckUri(deck.getId());
	}

	private ContentValues buildDeckValues() {
		ContentValues deckValues = new ContentValues();

		deckValues.put(GambitContract.Decks.TITLE, deck.getTitle());
		deckValues.put(GambitContract.Decks.CURRENT_CARD_INDEX, deck.getCurrentCardPosition());

		return deckValues;
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		if (silentMode == SilentMode.DISABLED) {
			BusProvider.getBus().post(busEvent);
		}
	}
}
