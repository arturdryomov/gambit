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
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.gambit.bus.BusEvent;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckCardsOrderLoadedEvent;
import ru.ming13.gambit.provider.GambitContract;

public class DeckCardsOrderLoadingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final Uri cardsUri;

	public static void execute(ContentResolver contentResolver, Uri cardsUri) {
		new DeckCardsOrderLoadingTask(contentResolver, cardsUri).execute();
	}

	private DeckCardsOrderLoadingTask(ContentResolver contentResolver, Uri cardsUri) {
		this.contentResolver = contentResolver;
		this.cardsUri = cardsUri;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		if (areCardsShuffled()) {
			return new DeckCardsOrderLoadedEvent(DeckCardsOrderLoadedEvent.CardsOrder.SHUFFLE);
		} else {
			return new DeckCardsOrderLoadedEvent(DeckCardsOrderLoadedEvent.CardsOrder.ORIGINAL);
		}
	}

	private boolean areCardsShuffled() {
		boolean cardsShuffled = false;

		Cursor cardsCursor = loadCards();

		while (cardsCursor.moveToNext()) {
			if (getCardOrderIndex(cardsCursor) != GambitContract.Cards.Defaults.ORDER_INDEX) {
				cardsShuffled = true;
			}
		}

		cardsCursor.close();

		return cardsShuffled;
	}

	private Cursor loadCards() {
		String[] projection = {GambitContract.Cards.ORDER_INDEX};

		return contentResolver.query(cardsUri, projection, null, null, null);
	}

	private int getCardOrderIndex(Cursor cardsCursor) {
		return cardsCursor.getInt(
			cardsCursor.getColumnIndex(GambitContract.Cards.ORDER_INDEX));
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
