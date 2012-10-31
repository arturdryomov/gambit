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


import java.util.List;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.seismic.ShakeDetector;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.model.Card;
import ru.ming13.gambit.local.model.Deck;
import ru.ming13.gambit.ui.intent.IntentException;
import ru.ming13.gambit.ui.intent.IntentExtras;
import ru.ming13.gambit.ui.loader.CardsLoader;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.loader.result.LoaderResult;
import ru.ming13.gambit.ui.adapter.CardsPagerAdapter;
import ru.ming13.gambit.ui.task.CurrentCardIndexChangingTask;


public class CardsPagerActivity extends SherlockFragmentActivity implements ShakeDetector.Listener, LoaderManager.LoaderCallbacks<LoaderResult<List<Card>>>
{
	private static enum CardsOrder
	{
		CURRENT, SHUFFLE, ORIGINAL
	}

	private Deck deck;

	private CardsOrder cardsOrder = CardsOrder.CURRENT;

	private SensorManager sensorManager;
	private ShakeDetector shakeDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager);

		deck = extractReceivedDeck();

		setUpShakeListener();

		populatePager(savedInstanceState);
	}

	private Deck extractReceivedDeck() {
		Deck deck = getIntent().getParcelableExtra(IntentExtras.DECK);

		if (deck == null) {
			throw new IntentException();
		}

		return deck;
	}

	private void setUpShakeListener() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		shakeDetector = new ShakeDetector(this);
	}

	@Override
	public void hearShake() {
		shuffleCards();
	}

	private void shuffleCards() {
		cardsOrder = CardsOrder.SHUFFLE;

		populatePager();
	}

	private void populatePager() {
		getSupportLoaderManager().restartLoader(Loaders.CARDS, null, this);
	}

	@Override
	public Loader<LoaderResult<List<Card>>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (cardsOrder) {
			case CURRENT:
				return CardsLoader.newCurrentOrderInstance(this, deck);

			case SHUFFLE:
				return CardsLoader.newShuffleOrderInstance(this, deck);

			case ORIGINAL:
				return CardsLoader.newOriginalOrderInstance(this, deck);

			default:
				return CardsLoader.newCurrentOrderInstance(this, deck);
		}
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult<List<Card>>> cardsLoader, LoaderResult<List<Card>> cardsLoaderResult) {
		List<Card> cards = cardsLoaderResult.getData();

		setUpCardsPagerAdapter(cards);
		setUpCurrentCardIndex();
	}

	private void setUpCardsPagerAdapter(List<Card> cards) {
		ViewPager cardsPager = getCardsPager();
		CardsPagerAdapter cardsPagerAdapter = new CardsPagerAdapter(getSupportFragmentManager(), cards);

		cardsPager.setAdapter(cardsPagerAdapter);

		cardsPager.getAdapter().notifyDataSetChanged();
	}

	private ViewPager getCardsPager() {
		return (ViewPager) findViewById(R.id.pager);
	}

	private void setUpCurrentCardIndex() {
		if (cardsOrder == CardsOrder.CURRENT) {
			int currentCardIndex = deck.getCurrentCardIndex();

			getCardsPager().setCurrentItem(currentCardIndex);
		}
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult<List<Card>>> cardsLoader) {
	}

	private void populatePager(Bundle savedInstanceState) {
		if (!isSavedInstanceStateValid(savedInstanceState)) {
			populatePager();
		}
		else {
			setUpCardsPagerAdapter(null);

			populatePager();
		}
	}

	private boolean isSavedInstanceStateValid(Bundle savedInstanceState) {
		return savedInstanceState != null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_cards_pager, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_back:
				setCurrentCardToFirst();
				return true;

			case R.id.menu_shuffle:
				shuffleCards();
				return true;

			case R.id.menu_reset:
				resetCardsOrder();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void setCurrentCardToFirst() {
		ViewPager cardsPager = getCardsPager();

		cardsPager.setCurrentItem(0);
	}

	private void resetCardsOrder() {
		cardsOrder = CardsOrder.ORIGINAL;

		populatePager();
	}

	@Override
	protected void onPause() {
		super.onPause();

		saveCurrentCardIndex();

		shakeDetector.stop();
	}

	private void saveCurrentCardIndex() {
		int currentCardIndex = getCardsPager().getCurrentItem();

		CurrentCardIndexChangingTask.newInstance(deck, currentCardIndex).execute();
	}

	@Override
	protected void onResume() {
		super.onResume();

		shakeDetector.start(sensorManager);
	}
}