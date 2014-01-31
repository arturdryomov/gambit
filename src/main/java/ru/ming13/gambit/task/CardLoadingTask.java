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
import android.util.Pair;

import ru.ming13.gambit.bus.BusEvent;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardLoadedEvent;
import ru.ming13.gambit.provider.GambitContract;


public class CardLoadingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final Uri cardUri;

	public static void execute(ContentResolver contentResolver, Uri cardUri) {
		new CardLoadingTask(contentResolver, cardUri).execute();
	}

	private CardLoadingTask(ContentResolver contentResolver, Uri cardUri) {
		this.contentResolver = contentResolver;
		this.cardUri = cardUri;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		Pair<String, String> cardSideTexts = queryCardSideTexts();

		return new CardLoadedEvent(cardSideTexts.first, cardSideTexts.second);
	}

	private Pair<String, String> queryCardSideTexts() {
		String[] projection = {GambitContract.Cards.FRONT_SIDE_TEXT, GambitContract.Cards.BACK_SIDE_TEXT};
		Cursor cardCursor = contentResolver.query(cardUri, projection, null, null, null);

		Pair<String, String> cardSidesText = getCardSideTexts(cardCursor);

		cardCursor.close();

		return cardSidesText;
	}

	private Pair<String, String> getCardSideTexts(Cursor cardCursor) {
		cardCursor.moveToFirst();

		String cardFrontSideText = cardCursor.getString(
			cardCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
		String cardBackSideText = cardCursor.getString(
			cardCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));

		return Pair.create(cardFrontSideText, cardBackSideText);
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
