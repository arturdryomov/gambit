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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckSelectedEvent;
import ru.ming13.gambit.fragment.CardsPagerFragment;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.util.Android;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;

public class DecksListActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decks);
	}

	@Subscribe
	public void onDeckSelected(DeckSelectedEvent event) {
		if (Android.with(this).isTablet() && Android.with(this).isLandscape()) {
			setUpCardsPagerFragment(event.getDeck());
		} else {
			startCardsPagerActivity(event.getDeck());
		}
	}

	private void setUpCardsPagerFragment(Deck deck) {
		Fragments.Operator.at(this).reset(buildCardsPagerFragment(deck), R.id.container_decks_pager);
	}

	private Fragment buildCardsPagerFragment(Deck deck) {
		return CardsPagerFragment.newInstance(deck);
	}

	private void startCardsPagerActivity(Deck deck) {
		Intent intent = Intents.Builder.with(this).buildCardsPagerIntent(deck);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_decks_list, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_create:
				startDeckCreationActivity();
				return true;

			case R.id.menu_backup:
				startBackupActivity();
				return true;

			case R.id.menu_rate_application:
				startApplicationRating();
				return true;

			case R.id.menu_send_feedback:
				startFeedbackSending();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void startDeckCreationActivity() {
		Intent intent = Intents.Builder.with(this).buildDeckCreationIntent();
		startActivity(intent);
	}

	private void startBackupActivity() {
		Intent intent = Intents.Builder.with(this).buildBackupIntent();
		startActivity(intent);
	}

	private void startApplicationRating() {
		try {
			Intent intent = Intents.Builder.with(this).buildGooglePlayAppIntent();
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Intent intent = Intents.Builder.with(this).buildGooglePlayWebIntent();
			startActivity(intent);
		}
	}

	private void startFeedbackSending() {
		Intent intent = Intents.Builder.with(this).buildFeedbackIntent();

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			startActivity(Intent.createChooser(intent, null));
		}
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
