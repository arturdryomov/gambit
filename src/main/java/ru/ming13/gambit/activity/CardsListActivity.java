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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ru.ming13.gambit.R;
import ru.ming13.gambit.fragment.CardsListFragment;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;


public class CardsListActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpFragment();
	}

	private void setUpFragment() {
		Fragments.Operator.set(this, buildFragment());
	}

	private Fragment buildFragment() {
		return CardsListFragment.newInstance(getCardsUri());
	}

	private Uri getCardsUri() {
		return GambitContract.Cards.getCardsUri(getDeckUri());
	}

	private Uri getDeckUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_cards_list, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			case R.id.menu_create:
				startCardCreationActivity();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void startCardCreationActivity() {
		Intent intent = Intents.Builder.with(this).buildCardCreationIntent(getCardsUri());
		startActivity(intent);
	}
}
