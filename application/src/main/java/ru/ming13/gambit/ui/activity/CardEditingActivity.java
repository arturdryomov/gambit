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
import android.widget.EditText;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.Card;
import ru.ming13.gambit.ui.intent.IntentException;
import ru.ming13.gambit.ui.intent.IntentExtras;


public class CardEditingActivity extends CardCreationActivity
{
	private Card card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpReceivedCardData();
	}

	@Override
	protected void performSubmitAction() {
		new UpdateCardTask().execute();
	}

	private class UpdateCardTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			card.setFrontSideText(frontSideText);
			card.setBackSideText(backSideText);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			finish();
		}
	}

	@Override
	protected void processReceivedData() {
		card = getIntent().getParcelableExtra(IntentExtras.CARD);

		if (card == null) {
			throw new IntentException();
		}
	}

	private void setUpReceivedCardData() {
		EditText frontSideTextEdit = (EditText) findViewById(R.id.edit_front_side_text);
		EditText backSideTextEdit = (EditText) findViewById(R.id.edit_back_side_text);

		frontSideTextEdit.setText(card.getFrontSideText());
		backSideTextEdit.setText(card.getBackSideText());
	}
}
