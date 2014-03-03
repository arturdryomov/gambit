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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import ru.ming13.gambit.fragment.CardsPagerFragment;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;

public class CardsPagerActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpFragment();
	}

	private void setUpFragment() {
		Fragments.Operator.at(this).set(buildFragment(), android.R.id.content);
	}

	private Fragment buildFragment() {
		return CardsPagerFragment.newInstance(getDeck());
	}

	private Deck getDeck() {
		return getIntent().getParcelableExtra(Intents.Extras.DECK);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				navigateUp();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void navigateUp() {
		NavUtils.navigateUpFromSameTask(this);
	}
}
