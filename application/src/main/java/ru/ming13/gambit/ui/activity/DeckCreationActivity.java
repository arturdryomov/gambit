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

package ru.ming13.gambit.ui.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.AlreadyExistsException;
import ru.ming13.gambit.local.DbProvider;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.intent.IntentFactory;


public class DeckCreationActivity extends FormActivity
{
	protected String deckName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_deck_creation);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void readUserDataFromFields() {
		deckName = getTextFromEdit(R.id.edit_deck_name);
	}

	@Override
	protected boolean isUserDataCorrect() {
		return !isDeckNameEmpty();
	}

	private boolean isDeckNameEmpty() {
		return TextUtils.isEmpty(deckName);
	}

	@Override
	protected void setUpErrorMessages() {
		if (isDeckNameEmpty()) {
			setDeckNameErrorMessage(getString(R.string.error_empty_field));
		}
	}

	protected void setDeckNameErrorMessage(String errorMessage) {
		EditText deckNameEdit = (EditText) findViewById(R.id.edit_deck_name);

		deckNameEdit.setError(errorMessage);
	}

	@Override
	protected void performSubmitAction() {
		new CreateDeckTask().execute();
	}

	private class CreateDeckTask extends AsyncTask<Void, Void, String>
	{
		private Deck deck;

		@Override
		protected String doInBackground(Void... params) {
			try {
				deck = DbProvider.getInstance().getDecks().createDeck(deckName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_deck_already_exists);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (TextUtils.isEmpty(errorMessage)) {
				callCardsEditing(deck);

				finish();
			}
			else {
				setDeckNameErrorMessage(errorMessage);
			}
		}
	}

	private void callCardsEditing(Deck deck) {
		Intent callIntent = IntentFactory.createCardsEditingIntent(this, deck);
		startActivity(callIntent);
	}
}
