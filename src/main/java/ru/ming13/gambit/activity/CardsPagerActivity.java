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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.gambit.R;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.util.Fragments;

public class CardsPagerActivity extends ActionBarActivity
{
	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	@InjectExtra(Fragments.Arguments.DECK)
	Deck deck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);

		setUpInjections();

		setUpToolbar();

		setUpFragment();
	}

	private void setUpInjections() {
		ButterKnife.inject(this);

		Dart.inject(this);
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
	}

	private void setUpFragment() {
		Fragments.Operator.at(this).set(getCardsPagerFragment(), R.id.container_fragment);
	}

	private Fragment getCardsPagerFragment() {
		return Fragments.Builder.buildCardsPagerFragment(deck);
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
