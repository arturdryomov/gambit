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
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;

import com.squareup.otto.Subscribe;
import com.viewpagerindicator.UnderlinePageIndicator;

import ru.ming13.gambit.R;
import ru.ming13.gambit.adapter.CardsPagerAdapter;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardCreationCalledEvent;
import ru.ming13.gambit.bus.DeckCardsOrderLoadedEvent;
import ru.ming13.gambit.bus.DeviceShakenEvent;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.DeckCardsOrderLoadingTask;
import ru.ming13.gambit.task.DeckCardsOrderResettingTask;
import ru.ming13.gambit.task.DeckCardsOrderShufflingTask;
import ru.ming13.gambit.task.DeckEditingTask;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.Loaders;
import ru.ming13.gambit.util.Seismometer;

public class CardsPagerActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static enum CardsOrder
	{
		DEFAULT, SHUFFLE, ORIGINAL
	}

	private CardsOrder currentCardsOrder = CardsOrder.DEFAULT;

	private Seismometer seismometer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cards_pager);

		setUpSeismometer();
		setUpCards();
	}

	private void setUpSeismometer() {
		seismometer = new Seismometer(this);
	}

	private void setUpCards() {
		setUpCardsAdapter();
		setUpCardsIndicator();
		setUpCardsContent();
	}

	private void setUpCardsAdapter() {
		getCardsPager().setAdapter(new CardsPagerAdapter(getFragmentManager()));
	}

	private ViewPager getCardsPager() {
		return (ViewPager) findViewById(R.id.pager_cards);
	}

	private void setUpCardsIndicator() {
		UnderlinePageIndicator cardsIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator_cards);
		cardsIndicator.setViewPager(getCardsPager());
	}

	private void setUpCardsContent() {
		getLoaderManager().initLoader(Loaders.CARDS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		Uri uri = getCardsUri();

		String[] projection = {GambitContract.Cards.FRONT_SIDE_TEXT, GambitContract.Cards.BACK_SIDE_TEXT};
		String sortOrder = String.format("%s, %s", GambitContract.Cards.ORDER_INDEX, GambitContract.Cards.FRONT_SIDE_TEXT);

		return new CursorLoader(this, uri, projection, null, null, sortOrder);
	}

	private Uri getCardsUri() {
		return GambitContract.Cards.getCardsUri(getDeck().getId());
	}

	private Deck getDeck() {
		return getIntent().getParcelableExtra(Intents.Extras.DECK);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cardsLoader, Cursor cardsCursor) {
		getCardsAdapter().swapCursor(cardsCursor);

		setUpCurrentCard();
		setUpCurrentCardsOrder();
	}

	private CardsPagerAdapter getCardsAdapter() {
		return (CardsPagerAdapter) getCardsPager().getAdapter();
	}

	private void setUpCurrentCard() {
		if (shouldCurrentCardBeSet()) {
			setUpCurrentCard(getDeck().getCurrentCardPosition());
		}
	}

	private boolean shouldCurrentCardBeSet() {
		return (currentCardsOrder == CardsOrder.DEFAULT) && (getCardsPager().getCurrentItem() == 0);
	}

	private void setUpCurrentCard(int currentCard) {
		getCardsPager().setCurrentItem(currentCard);
	}

	private void setUpCurrentCardsOrder() {
		if (shouldCurrentCardsOrderBeSet()) {
			DeckCardsOrderLoadingTask.execute(getContentResolver(), getDeck());
		}
	}

	private boolean shouldCurrentCardsOrderBeSet() {
		return currentCardsOrder == CardsOrder.DEFAULT;
	}

	@Subscribe
	public void onCardsOrderLoaded(DeckCardsOrderLoadedEvent event) {
		switch (event.getCardsOrder()) {
			case SHUFFLE:
				currentCardsOrder = CardsOrder.SHUFFLE;
				break;

			case ORIGINAL:
				currentCardsOrder = CardsOrder.ORIGINAL;
				break;

			default:
				break;
		}

		setUpCurrentActionBar();
	}

	private void setUpCurrentActionBar() {
		invalidateOptionsMenu();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cardsLoader) {
		getCardsAdapter().swapCursor(null);
	}

	@Subscribe
	public void onCardCreationCalled(CardCreationCalledEvent event) {
		startCardCreationStack();
	}

	private void startCardCreationStack() {
		startActivities(new Intent[]{
			Intents.Builder.with(this).buildCardsListIntent(getDeck()),
			Intents.Builder.with(this).buildCardCreationIntent(getDeck())});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (shouldActionsBeShown()) {
			getMenuInflater().inflate(R.menu.action_bar_cards_pager, menu);

			menu.findItem(R.id.menu_shuffle).setIcon(getShuffleActionIconResource());
			menu.findItem(R.id.menu_shuffle).setTitle(getShuffleActionTitleResource());
		}

		return super.onCreateOptionsMenu(menu);
	}

	private boolean shouldActionsBeShown() {
		return getCardsAdapter().getCount() > 1;
	}

	private int getShuffleActionIconResource() {
		switch (currentCardsOrder) {
			case SHUFFLE:
				return R.drawable.ic_menu_shuffle_enabled;

			default:
				return R.drawable.ic_menu_shuffle_disabled;
		}
	}

	private int getShuffleActionTitleResource() {
		switch (currentCardsOrder) {
			case SHUFFLE:
				return R.string.menu_shuffle_disable;

			default:
				return R.string.menu_shuffle_enable;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				navigateUp();
				return true;

			case R.id.menu_replay:
				replayCards();
				return true;

			case R.id.menu_shuffle:
				switchCardsOrder();
				return true;

			case R.id.menu_flip:
				flipCards();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void navigateUp() {
		NavUtils.navigateUpFromSameTask(this);
	}

	private void replayCards() {
		getCardsPager().setCurrentItem(0);
	}

	private void switchCardsOrder() {
		switch (currentCardsOrder) {
			case ORIGINAL:
				shuffleCards();
				break;

			default:
				orderCards();
				break;
		}
	}

	private void shuffleCards() {
		DeckCardsOrderShufflingTask.execute(getContentResolver(), getDeck());

		switchCardsOrder(CardsOrder.SHUFFLE);
	}

	private void switchCardsOrder(CardsOrder cardsOrder) {
		currentCardsOrder = cardsOrder;

		animateCardsShaking();

		setUpCurrentActionBar();
	}

	private void animateCardsShaking() {
		getCardsPager().startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
	}

	private void orderCards() {
		DeckCardsOrderResettingTask.execute(getContentResolver(), getDeck());

		switchCardsOrder(CardsOrder.ORIGINAL);
	}

	@Subscribe
	public void onDeviceShaken(DeviceShakenEvent event) {
		shuffleCards();
	}

	private void flipCards() {
		getCardsAdapter().switchDefaultCardSide();
		getCardsAdapter().notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();

		seismometer.enable();

		BusProvider.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		seismometer.disable();

		BusProvider.getBus().unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		saveCurrentCard();
	}

	private void saveCurrentCard() {
		Deck deck = new Deck(getDeck().getId(), getDeck().getTitle(), getCardsPager().getCurrentItem());

		DeckEditingTask.execute(getContentResolver(), deck);
	}
}
