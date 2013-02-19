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
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.DeckTitleQueriedEvent;


public class DeckTitleQueryingTask extends AsyncTask<Void, Void, String>
{
	private final ContentResolver contentResolver;
	private final Uri deckUri;

	public static void execute(ContentResolver contentResolver, Uri deckUri) {
		new DeckTitleQueryingTask(contentResolver, deckUri).execute();
	}

	private DeckTitleQueryingTask(ContentResolver contentResolver, Uri deckUri) {
		this.contentResolver = contentResolver;
		this.deckUri = deckUri;
	}

	@Override
	protected String doInBackground(Void... parameters) {
		return queryDeckTitle();
	}

	private String queryDeckTitle() {
		String[] projection = {GambitContract.Decks.TITLE};
		Cursor deckCursor = contentResolver.query(deckUri, projection, null, null, null);

		String deckTitle = extractDeckTitle(deckCursor);

		deckCursor.close();

		return deckTitle;
	}

	private String extractDeckTitle(Cursor deckCursor) {
		deckCursor.moveToFirst();

		return deckCursor.getString(deckCursor.getColumnIndex(GambitContract.Decks.TITLE));
	}

	@Override
	protected void onPostExecute(String deckTitle) {
		super.onPostExecute(deckTitle);

		BusProvider.getInstance().post(new DeckTitleQueriedEvent(deckTitle));
	}
}
