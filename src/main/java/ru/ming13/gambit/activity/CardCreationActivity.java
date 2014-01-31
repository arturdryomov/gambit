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

package ru.ming13.gambit.activity;


import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

import com.squareup.otto.Subscribe;

import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardAssembledEvent;
import ru.ming13.gambit.bus.CardSavedEvent;
import ru.ming13.gambit.bus.OperationCancelledEvent;
import ru.ming13.gambit.fragment.CardOperationFragment;
import ru.ming13.gambit.task.CardCreationTask;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.OperationBar;


public class CardCreationActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpBar();
		setUpFragment();
	}

	private void setUpBar() {
		OperationBar.at(this).show();
	}

	private void setUpFragment() {
		Fragments.Operator.set(this, buildFragment());
	}

	private Fragment buildFragment() {
		return CardOperationFragment.newInstance();
	}

	@Subscribe
	public void onOperationCancelled(OperationCancelledEvent event) {
		finish();
	}

	@Subscribe
	public void onCardAssembled(CardAssembledEvent event) {
		saveCard(event.getCardFrontSideText(), event.getCardBackSideText());
	}

	private void saveCard(String cardFrontSideText, String cardBackSideText) {
		CardCreationTask.execute(getContentResolver(), getCardsUri(), cardFrontSideText, cardBackSideText);
	}

	private Uri getCardsUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
	}

	@Subscribe
	public void onCardSaved(CardSavedEvent event) {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
