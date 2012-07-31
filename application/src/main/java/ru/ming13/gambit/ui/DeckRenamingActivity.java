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

package ru.ming13.gambit.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.AlreadyExistsException;
import ru.ming13.gambit.local.Deck;


public class DeckRenamingActivity extends DeckCreationActivity
{
	private Deck deck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		processReceivedDeck();
		setUpReceivedDeckData();
	}

	@Override
	protected void performSubmitAction() {
		new UpdateDeckTask().execute();
	}

	private class UpdateDeckTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				deck.setTitle(deckName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_deck_already_exists);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (TextUtils.isEmpty(errorMessage)) {
				finish();
			}
			else {
				UserAlerter.alert(DeckRenamingActivity.this, errorMessage);
			}
		}
	}

	private void processReceivedDeck() {
		try {
			deck = (Deck) IntentProcessor.getMessage(this);
		}
		catch (IntentCorruptedException e) {
			UserAlerter.alert(this, R.string.error_unspecified);

			finish();
		}
	}

	private void setUpReceivedDeckData() {
		EditText deckNameEdit = (EditText) findViewById(R.id.edit_deck_name);

		deckNameEdit.setText(deck.getTitle());
	}
}
