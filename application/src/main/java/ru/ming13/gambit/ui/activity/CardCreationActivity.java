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


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.IntentCorruptedException;
import ru.ming13.gambit.ui.IntentProcessor;
import ru.ming13.gambit.ui.UserAlerter;


public class CardCreationActivity extends FormActivity
{
	private Deck deck;

	protected String frontSideText;
	protected String backSideText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_card_creation);
		super.onCreate(savedInstanceState);

		processReceivedData();
	}

	@Override
	protected void readUserDataFromFields() {
		frontSideText = getTextFromEdit(R.id.edit_front_side_text);
		backSideText = getTextFromEdit(R.id.edit_back_side_text);
	}

	@Override
	protected boolean isUserDataCorrect() {
		return !isFrontSideTextEmpty() && !isBackSideTextEmpty();
	}

	private boolean isFrontSideTextEmpty() {
		return TextUtils.isEmpty(frontSideText);
	}

	private boolean isBackSideTextEmpty() {
		return TextUtils.isEmpty(backSideText);
	}

	@Override
	protected void setUpErrorMessages() {
		if (isFrontSideTextEmpty()) {
			setFrontSideTextErrorMessage(getString(R.string.error_empty_field));
		}

		if (isBackSideTextEmpty()) {
			setBackSideTextErrorMessage(getString(R.string.error_empty_field));
		}
	}

	private void setFrontSideTextErrorMessage(String errorMessage) {
		EditText frontSideTextEdit = (EditText) findViewById(R.id.edit_front_side_text);

		frontSideTextEdit.setError(errorMessage);
	}

	private void setBackSideTextErrorMessage(String errorMessage) {
		EditText backSideTextEdit = (EditText) findViewById(R.id.edit_back_side_text);

		backSideTextEdit.setError(errorMessage);
	}

	@Override
	protected void performSubmitAction() {
		new CreateCardTask().execute();
	}

	private class CreateCardTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			deck.createCard(frontSideText, backSideText);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			finish();
		}
	}

	protected void processReceivedData() {
		try {
			deck = (Deck) IntentProcessor.getMessage(this);
		}
		catch (IntentCorruptedException e) {
			UserAlerter.alert(this, R.string.error_unspecified);

			finish();
		}
	}
}
