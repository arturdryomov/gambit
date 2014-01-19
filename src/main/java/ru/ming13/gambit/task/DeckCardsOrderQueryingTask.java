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
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.bus.BusEvent;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckCardsOrderQueriedEvent;


public class DeckCardsOrderQueryingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final Uri cardsUri;

	public static void execute(ContentResolver contentResolver, Uri cardsUri) {
		new DeckCardsOrderQueryingTask(contentResolver, cardsUri).execute();
	}

	private DeckCardsOrderQueryingTask(ContentResolver contentResolver, Uri cardsUri) {
		this.contentResolver = contentResolver;
		this.cardsUri = cardsUri;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		if (areCardsShuffled()) {
			return new DeckCardsOrderQueriedEvent(DeckCardsOrderQueriedEvent.CardsOrder.SHUFFLE);
		}
		else {
			return new DeckCardsOrderQueriedEvent(DeckCardsOrderQueriedEvent.CardsOrder.ORIGINAL);
		}
	}

	private boolean areCardsShuffled() {
		boolean cardsShuffled = false;

		Cursor cardsCursor = queryCards();

		while (cardsCursor.moveToNext()) {
			int cardOrderIndex = cardsCursor.getInt(
				cardsCursor.getColumnIndex(GambitContract.Cards.ORDER_INDEX));

			if (cardOrderIndex != GambitContract.Cards.DEFAULT_ORDER_INDEX) {
				cardsShuffled = true;
			}
		}

		cardsCursor.close();

		return cardsShuffled;
	}

	private Cursor queryCards() {
		String[] projection = {GambitContract.Cards.ORDER_INDEX};

		return contentResolver.query(cardsUri, projection, null, null, null);
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getInstance().post(busEvent);
	}
}
