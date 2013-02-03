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


import android.content.Intent;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.squareup.seismic.ShakeDetector;
import com.viewpagerindicator.UnderlinePageIndicator;
import ru.ming13.gambit.R;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.ui.adapter.CardsPagerAdapter;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.CardsListCalledFromCardsEmptyPagerEvent;
import ru.ming13.gambit.ui.bus.DeckCurrentCardQueriedEvent;
import ru.ming13.gambit.ui.intent.IntentException;
import ru.ming13.gambit.ui.intent.IntentExtras;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.task.DeckCardsOrderResettingTask;
import ru.ming13.gambit.ui.task.DeckCardsOrderShufflingTask;
import ru.ming13.gambit.ui.task.DeckCurrentCardQueryingTask;
import ru.ming13.gambit.ui.task.DeckCurrentCardSavingTask;


public class CardsPagerActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, ShakeDetector.Listener
{
	private static enum CardsOrder
	{
		DEFAULT, SHUFFLE, ORIGINAL
	}

	private Uri cardsUri;

	private CardsOrder cardsOrder = CardsOrder.DEFAULT;

	private SensorManager sensorManager;
	private ShakeDetector shakeDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager);

		setUpCardsUri();

		loadCards();

		setUpShakeListener();
	}

	private void setUpCardsUri() {
		Uri deckUri = extractReceivedDeckUri();

		cardsUri = GambitContract.Cards.buildCardsUri(deckUri);
	}

	private Uri extractReceivedDeckUri() {
		Uri deckUri = getIntent().getParcelableExtra(IntentExtras.DECK_URI);

		if (deckUri == null) {
			throw new IntentException();
		}

		return deckUri;
	}

	private void loadCards() {
		getSupportLoaderManager().initLoader(Loaders.CARDS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		String[] projection = {GambitContract.Cards.FRONT_SIDE_TEXT, GambitContract.Cards.BACK_SIDE_TEXT};
		String sortOrder = String.format("%s, %s", GambitContract.Cards.ORDER_INDEX,
			GambitContract.Cards.FRONT_SIDE_TEXT);

		return new CursorLoader(this, cardsUri, projection, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cardsLoader, Cursor cardsCursor) {
		setUpCardsPagerAdapter(cardsCursor);

		updateActionBarItems();

		if (getCardsPagerAdapter().isEmpty()) {
			return;
		}

		setUpCardsPagerIndicator();
		setUpCurrentCardIndex();
	}

	private void setUpCardsPagerAdapter(Cursor cardsCursor) {
		if (!isSettingCardsPagerAdapterRequired()) {
			getCardsPagerAdapter().swapCursor(cardsCursor);
			return;
		}

		CardsPagerAdapter adapter = new CardsPagerAdapter(getSupportFragmentManager(), cardsCursor);

		getCardsPager().setAdapter(adapter);
	}

	private boolean isSettingCardsPagerAdapterRequired() {
		// Avoid setting adapter after orientation change: ViewPager saves adapter itself

		return (cardsOrder != CardsOrder.DEFAULT) || (getCardsPager().getCurrentItem() == 0);
	}

	private ViewPager getCardsPager() {
		return (ViewPager) findViewById(R.id.pager);
	}

	private CardsPagerAdapter getCardsPagerAdapter() {
		return (CardsPagerAdapter) getCardsPager().getAdapter();
	}

	private void updateActionBarItems() {
		supportInvalidateOptionsMenu();
	}

	private void setUpCardsPagerIndicator() {
		UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);

		indicator.setViewPager(getCardsPager());
	}

	private void setUpCurrentCardIndex() {
		if (!isSettingCurrentCardIndexRequired()) {
			return;
		}

		Uri deckUri = GambitContract.Decks.buildDeckUri(GambitContract.Cards.getDeckId(cardsUri));

		DeckCurrentCardQueryingTask.execute(getContentResolver(), deckUri);
	}

	private boolean isSettingCurrentCardIndexRequired() {
		// Avoid case when user changed order or current card already

		return (cardsOrder == CardsOrder.DEFAULT) && (getCardsPager().getCurrentItem() == 0);
	}

	@Subscribe
	public void onCurrentCardQueried(DeckCurrentCardQueriedEvent deckCurrentCardQueriedEvent) {
		int currentCardIndex = deckCurrentCardQueriedEvent.getCurrentCardIndex();

		setUpCurrentCardIndex(currentCardIndex);
	}

	private void setUpCurrentCardIndex(int currentCardIndex) {
		if (!isSettingCurrentCardIndexRequired()) {
			return;
		}

		getCardsPager().setCurrentItem(currentCardIndex);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cardsLoader) {
		getCardsPagerAdapter().swapCursor(null);
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

		DeckCardsOrderShufflingTask.execute(getContentResolver(), cardsUri);

		showCardsOrderChangingAnimation();
	}

	private void showCardsOrderChangingAnimation() {
		Animation shakingAnimation = AnimationUtils.loadAnimation(this, R.anim.shaking);

		getCardsPager().startAnimation(shakingAnimation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if ((getCardsPagerAdapter() != null) && (getCardsPagerAdapter().isEmpty())) {
			return false;
		}

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
		getCardsPager().setCurrentItem(0);
	}

	private void resetCardsOrder() {
		cardsOrder = CardsOrder.ORIGINAL;

		DeckCardsOrderResettingTask.execute(getContentResolver(), cardsUri);

		showCardsOrderChangingAnimation();
	}

	@Subscribe
	public void onCardsListCalledFromCardsEmptyPager(CardsListCalledFromCardsEmptyPagerEvent cardsListCalledFromCardsEmptyPagerEvent) {
		callCardsList();
	}

	private void callCardsList() {
		Uri deckUri = GambitContract.Decks.buildDeckUri(GambitContract.Cards.getDeckId(cardsUri));

		Intent intent = IntentFactory.createCardsIntent(this, deckUri);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		shakeDetector.start(sensorManager);

		BusProvider.getInstance().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		shakeDetector.stop();

		BusProvider.getInstance().unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		saveCurrentCardIndex();
	}

	private void saveCurrentCardIndex() {
		Uri deckUri = GambitContract.Decks.buildDeckUri(GambitContract.Cards.getDeckId(cardsUri));
		int currentCardIndex = getCardsPager().getCurrentItem();

		DeckCurrentCardSavingTask.execute(getContentResolver(), deckUri, currentCardIndex);
	}
}