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


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;

import com.squareup.otto.Subscribe;

import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardCreatedEvent;
import ru.ming13.gambit.bus.CardCreationCancelledEvent;
import ru.ming13.gambit.fragment.CardCreationFragment;
import ru.ming13.gambit.intent.IntentException;
import ru.ming13.gambit.intent.IntentExtras;
import ru.ming13.gambit.intent.IntentFactory;


public class CardCreationActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return CardCreationFragment.newInstance(extractReceivedCardsUri());
	}

	private Uri extractReceivedCardsUri() {
		Uri cardsUri = getIntent().getParcelableExtra(IntentExtras.CARDS_URI);

		if (cardsUri == null) {
			throw new IntentException();
		}

		return cardsUri;
	}

	@Subscribe
	public void onCardCreated(CardCreatedEvent cardCreatedEvent) {
		callCardsList();

		finish();
	}

	private void callCardsList() {
		Intent intent = IntentFactory.createCardsIntent(this, buildDeckUri());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private Uri buildDeckUri() {
		long deckId = GambitContract.Cards.getDeckId(extractReceivedCardsUri());

		return GambitContract.Decks.buildDeckUri(deckId);
	}

	@Subscribe
	public void onCardCreationCancelled(CardCreationCancelledEvent cardCreationCancelledEvent) {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getInstance().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getInstance().unregister(this);
	}
}
