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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckSelectedEvent;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.util.Android;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;

public class DecksListActivity extends ActionBarActivity
{
	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decks);

		setUpInjections();

		setUpToolbar();

		setUpFragments();
	}

	private void setUpInjections() {
		ButterKnife.inject(this);
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);
	}

	private void setUpFragments() {
		if (Android.isTablet(this) && Android.isLandscape(this)) {
			Fragments.Operator.at(this).set(R.id.container_decks, getDecksListFragment());

			Fragments.Operator.at(this).set(R.id.container_cards, getMessageFragment());
		} else {
			Fragments.Operator.at(this).set(R.id.container_fragment, getDecksListFragment());
		}
	}

	private Fragment getDecksListFragment() {
		return Fragments.Builder.buildDecksListFragment();
	}

	private Fragment getMessageFragment() {
		return Fragments.Builder.buildMessageFragment(getString(R.string.empty_deck_selection));
	}

	@Subscribe
	public void onDeckSelected(DeckSelectedEvent event) {
		setUpCardsPager(event.getDeck());
	}

	private void setUpCardsPager(Deck deck) {
		if (Android.isTablet(this) && Android.isLandscape(this)) {
			setUpCardsPagerFragment(deck);
		} else {
			startCardsPagerActivity(deck);
		}
	}

	private void setUpCardsPagerFragment(Deck deck) {
		Fragments.Operator.at(this).reset(R.id.container_cards, getCardsPagerFragment(deck));
	}

	private Fragment getCardsPagerFragment(Deck deck) {
		return Fragments.Builder.buildCardsPagerFragment(deck);
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
