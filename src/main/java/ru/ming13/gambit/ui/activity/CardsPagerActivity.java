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
import android.support.v4.app.NavUtils;
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
import ru.ming13.gambit.ui.bus.DeckCardsOrderQueriedEvent;
import ru.ming13.gambit.ui.bus.DeckCurrentCardQueriedEvent;
import ru.ming13.gambit.ui.intent.IntentException;
import ru.ming13.gambit.ui.intent.IntentExtras;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.task.DeckCardsOrderQueryingTask;
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

		setUpHomeButton();

		setUpCardsUri();

		loadCards();

		setUpShakeListener();
	}

	private void setUpHomeButton() {
		getSupportActionBar().setHomeButtonEnabled(true);
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

		invalidateActionBarItems();

		if (getCardsPagerAdapter().isEmpty()) {
			return;
		}

		setUpCurrentCardIndex();
		setUpCardsOrder();
		setUpCardsPagerIndicator();
	}

	private void setUpCardsPagerAdapter(Cursor cardsCursor) {
		if (isOnlyCardsPagerAdapterCursorUpdatingRequired()) {
			getCardsPagerAdapter().swapCursor(cardsCursor);
			return;
		}

		CardsPagerAdapter adapter = new CardsPagerAdapter(getSupportFragmentManager(), cardsCursor);
		getCardsPager().setAdapter(adapter);
	}

	private boolean isOnlyCardsPagerAdapterCursorUpdatingRequired() {
		// Avoid setting adapter after orientation change: ViewPager saves adapter itself

		return (cardsOrder == CardsOrder.DEFAULT) && (getCardsPager().getCurrentItem() != 0);
	}

	private ViewPager getCardsPager() {
		return (ViewPager) findViewById(R.id.pager);
	}

	private CardsPagerAdapter getCardsPagerAdapter() {
		return (CardsPagerAdapter) getCardsPager().getAdapter();
	}

	private void invalidateActionBarItems() {
		supportInvalidateOptionsMenu();
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
	public void onCurrentCardQueried(DeckCurrentCardQueriedEvent currentCardQueriedEvent) {
		int currentCardIndex = currentCardQueriedEvent.getCurrentCardIndex();

		setUpCurrentCardIndex(currentCardIndex);
	}

	private void setUpCurrentCardIndex(int currentCardIndex) {
		getCardsPager().setCurrentItem(currentCardIndex);
	}

	private void setUpCardsOrder() {
		if (cardsOrder != CardsOrder.DEFAULT) {
			return;
		}

		DeckCardsOrderQueryingTask.execute(getContentResolver(), cardsUri);
	}

	@Subscribe
	public void onCardsOrderQueriedEvent(DeckCardsOrderQueriedEvent cardsOrderQueriedEvent) {
		switch (cardsOrderQueriedEvent.getCardsOrder()) {
			case SHUFFLE:
				cardsOrder = CardsOrder.SHUFFLE;
				break;

			case ORIGINAL:
				cardsOrder = CardsOrder.ORIGINAL;
				break;

			default:
				break;
		}

		invalidateActionBarItems();
	}

	private void setUpCardsPagerIndicator() {
		UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(getCardsPager());
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
		if (!isShowingActionBarButtonsRequired()) {
			return false;
		}

		switch (cardsOrder) {
			case SHUFFLE:
				getSupportMenuInflater().inflate(R.menu.menu_action_bar_cards_pager_shuffle_enabled, menu);
				return true;

			case ORIGINAL:
				getSupportMenuInflater().inflate(R.menu.menu_action_bar_cards_pager_shuffle_disabled, menu);
				return true;

			case DEFAULT:
				getSupportMenuInflater().inflate(R.menu.menu_action_bar_cards_pager_shuffle_disabled, menu);
				return true;

			default:
				return false;
		}
	}

	private boolean isShowingActionBarButtonsRequired() {
		CardsPagerAdapter cardsPagerAdapter = getCardsPagerAdapter();

		if (cardsPagerAdapter == null) {
			return false;
		}

		if (cardsPagerAdapter.isEmpty()) {
			return false;
		}

		if (cardsPagerAdapter.getCount() == 1) {
			return false;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				navigateUp();
				return true;

			case R.id.menu_replay:
				setCurrentCardToFirst();
				return true;

			case R.id.menu_shuffle:
				changeCardsOrder();
				invalidateActionBarItems();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void navigateUp() {
		Intent intent = IntentFactory.createDecksIntent(this);
		NavUtils.navigateUpTo(this, intent);
	}

	private void setCurrentCardToFirst() {
		getCardsPager().setCurrentItem(0);
	}

	private void changeCardsOrder() {
		switch (cardsOrder) {
			case SHUFFLE:
				resetCardsOrder();
				break;

			case ORIGINAL:
				shuffleCards();
				break;

			default:
				break;
		}
	}

	private void resetCardsOrder() {
		cardsOrder = CardsOrder.ORIGINAL;

		DeckCardsOrderResettingTask.execute(getContentResolver(), cardsUri);

		showCardsOrderChangingAnimation();
	}

	@Subscribe
	public void onCardsListCalledFromCardsEmptyPager(CardsListCalledFromCardsEmptyPagerEvent cardsListCalledFromCardsEmptyPagerEvent) {
		callCardCreation();
	}

	private void callCardCreation() {
		Intent intent = IntentFactory.createCardCreationIntent(this, cardsUri);
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
	protected void onStop() {
		super.onStop();

		saveCurrentCardIndex();
	}

	private void saveCurrentCardIndex() {
		Uri deckUri = GambitContract.Decks.buildDeckUri(GambitContract.Cards.getDeckId(cardsUri));
		int currentCardIndex = getCardsPager().getCurrentItem();

		DeckCurrentCardSavingTask.execute(getContentResolver(), deckUri, currentCardIndex);
	}
}