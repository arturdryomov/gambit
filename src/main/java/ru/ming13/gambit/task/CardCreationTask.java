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
import ru.ming13.gambit.bus.CardSavedEvent;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;

public class CardCreationTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final Deck deck;
	private final Card card;

	public static void execute(ContentResolver contentResolver, Deck deck, Card card) {
		new CardCreationTask(contentResolver, deck, card).execute();
	}

	private CardCreationTask(ContentResolver contentResolver, Deck deck, Card card) {
		this.contentResolver = contentResolver;
		this.deck = deck;
		this.card = card;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		createCard();

		return new CardSavedEvent();
	}

	private void createCard() {
		contentResolver.insert(buildCardsUri(), buildCardValues());
	}

	private Uri buildCardsUri() {
		return GambitContract.Cards.getCardsUri(deck.getId());
	}

	private ContentValues buildCardValues() {
		ContentValues cardValues = new ContentValues();

		cardValues.put(GambitContract.Cards.FRONT_SIDE_TEXT, card.getFrontSideText());
		cardValues.put(GambitContract.Cards.BACK_SIDE_TEXT, card.getBackSideText());
		cardValues.put(GambitContract.Cards.DECK_ID, deck.getId());
		cardValues.put(GambitContract.Cards.ORDER_INDEX, getCardOrderIndex());

		return cardValues;
	}

	private int getCardOrderIndex() {
		return GambitContract.Cards.Defaults.ORDER_INDEX;
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
