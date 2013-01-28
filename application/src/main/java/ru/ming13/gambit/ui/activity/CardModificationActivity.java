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


import android.net.Uri;
import android.support.v4.app.Fragment;
import com.squareup.otto.Subscribe;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.CardEditedEvent;
import ru.ming13.gambit.ui.bus.CardEditingCancelledEvent;
import ru.ming13.gambit.ui.fragment.CardEditingFragment;
import ru.ming13.gambit.ui.intent.IntentException;
import ru.ming13.gambit.ui.intent.IntentExtras;


public class CardModificationActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return CardEditingFragment.newInstance(extractReceivedCardUri());
	}

	private Uri extractReceivedCardUri() {
		Uri cardUri = getIntent().getParcelableExtra(IntentExtras.CARD_URI);

		if (cardUri == null) {
			throw new IntentException();
		}

		return cardUri;
	}

	@Subscribe
	public void onCardEdited(CardEditedEvent cardEditedEvent) {
		finish();
	}

	@Subscribe
	public void onCardEditingCancelled(CardEditingCancelledEvent cardEditingCancelledEvent) {
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
