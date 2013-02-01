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
import android.net.Uri;
import android.os.AsyncTask;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.CardCreatedEvent;


public class CardCreationTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final Uri cardsUri;
	private final String cardFrontSideText;
	private final String cardBackSideText;

	public static void execute(ContentResolver contentResolver, Uri cardsUri, String cardFrontSideText, String cardBackSideText) {
		new CardCreationTask(contentResolver, cardsUri, cardFrontSideText, cardBackSideText).execute();
	}

	private CardCreationTask(ContentResolver contentResolver, Uri cardsUri, String cardFrontSideText, String cardBackSideText) {
		this.contentResolver = contentResolver;
		this.cardsUri = cardsUri;
		this.cardFrontSideText = cardFrontSideText;
		this.cardBackSideText = cardBackSideText;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		createCard();

		return null;
	}

	private void createCard() {
		contentResolver.insert(cardsUri, buildCardValues());
	}

	private ContentValues buildCardValues() {
		ContentValues cardValues = new ContentValues();

		cardValues.put(GambitContract.Cards.FRONT_SIDE_TEXT, cardFrontSideText);
		cardValues.put(GambitContract.Cards.BACK_SIDE_TEXT, cardBackSideText);

		return cardValues;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		BusProvider.getInstance().post(new CardCreatedEvent());
	}
}
