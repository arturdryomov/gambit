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
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.CardSidesQueriedEvent;


public class CardSidesQueryingTask extends AsyncTask<Void, Void, Pair<String, String>>
{
	private final ContentResolver contentResolver;
	private final Uri cardUri;

	public static void execute(ContentResolver contentResolver, Uri cardUri) {
		new CardSidesQueryingTask(contentResolver, cardUri).execute();
	}

	private CardSidesQueryingTask(ContentResolver contentResolver, Uri cardUri) {
		this.contentResolver = contentResolver;
		this.cardUri = cardUri;
	}

	@Override
	protected Pair<String, String> doInBackground(Void... parameters) {
		return queryCardSidesText();
	}

	private Pair<String, String> queryCardSidesText() {
		String[] projection = {GambitContract.Cards.FRONT_SIDE_TEXT, GambitContract.Cards.BACK_SIDE_TEXT};
		Cursor cardCursor = contentResolver.query(cardUri, projection, null, null, null);

		Pair<String, String> cardSidesText = extractCardSidesText(cardCursor);

		cardCursor.close();

		return cardSidesText;
	}

	private Pair<String, String> extractCardSidesText(Cursor cardCursor) {
		cardCursor.moveToFirst();

		String cardFrontSideText = cardCursor.getString(
			cardCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
		String cardBackSideText = cardCursor.getString(
			cardCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));

		return new Pair<String, String>(cardFrontSideText, cardBackSideText);
	}

	@Override
	protected void onPostExecute(Pair<String, String> cardSidesText) {
		super.onPostExecute(cardSidesText);

		BusProvider.getInstance().post(
			new CardSidesQueriedEvent(cardSidesText.first, cardSidesText.second));
	}
}
