package ru.ming13.gambit.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;

import com.squareup.otto.Subscribe;
import com.viewpagerindicator.UnderlinePageIndicator;

import ru.ming13.gambit.R;
import ru.ming13.gambit.adapter.CardsPagerAdapter;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckCardsOrderLoadedEvent;
import ru.ming13.gambit.bus.DeckCurrentCardLoadedEvent;
import ru.ming13.gambit.bus.DeviceShakeEvent;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.DeckCardsOrderLoadingTask;
import ru.ming13.gambit.task.DeckCardsOrderResettingTask;
import ru.ming13.gambit.task.DeckCardsOrderShufflingTask;
import ru.ming13.gambit.task.DeckCurrentCardLoadingTask;
import ru.ming13.gambit.task.DeckCurrentCardSavingTask;
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
		setContentView(R.layout.activity_pager);

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
		getCardsPager().setAdapter(new CardsPagerAdapter(getFragmentManager(), null));
	}

	private ViewPager getCardsPager() {
		return (ViewPager) findViewById(R.id.pager);
	}

	private void setUpCardsIndicator() {
		UnderlinePageIndicator cardsIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
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
		return GambitContract.Cards.getCardsUri(getDeckUri());
	}

	private Uri getDeckUri() {
		return getIntent().getParcelableExtra(Intents.Extras.URI);
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
			DeckCurrentCardLoadingTask.execute(getContentResolver(), getDeckUri());
		}
	}

	private boolean shouldCurrentCardBeSet() {
		return (currentCardsOrder == CardsOrder.DEFAULT) && (getCardsPager().getCurrentItem() == 0);
	}

	@Subscribe
	public void onCurrentCardLoaded(DeckCurrentCardLoadedEvent event) {
		setUpCurrentCard(event.getCurrentCardIndex());
	}

	private void setUpCurrentCard(int currentCard) {
		getCardsPager().setCurrentItem(currentCard);
	}

	private void setUpCurrentCardsOrder() {
		if (shouldCurrentCardsOrderBeSet()) {
			DeckCardsOrderLoadingTask.execute(getContentResolver(), getCardsUri());
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

		changeAvailableActions();
	}

	private void changeAvailableActions() {
		invalidateOptionsMenu();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cardsLoader) {
		getCardsAdapter().swapCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (shouldActionsBeShown()) {
			getMenuInflater().inflate(R.menu.menu_action_bar_cards_pager, menu);

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
				finish();
				return true;

			case R.id.menu_replay:
				replayCards();
				return true;

			case R.id.menu_shuffle:
				switchCardsOrder();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
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
		DeckCardsOrderShufflingTask.execute(getContentResolver(), getCardsUri());

		switchCardsOrder(CardsOrder.SHUFFLE);
	}

	private void switchCardsOrder(CardsOrder cardsOrder) {
		currentCardsOrder = cardsOrder;

		animateCardsShaking();

		changeAvailableActions();
	}

	private void animateCardsShaking() {
		getCardsPager().startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
	}

	private void orderCards() {
		DeckCardsOrderResettingTask.execute(getContentResolver(), getCardsUri());

		switchCardsOrder(CardsOrder.ORIGINAL);
	}

	@Subscribe
	public void onDeviceShaked(DeviceShakeEvent event) {
		shuffleCards();
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
		DeckCurrentCardSavingTask.execute(getContentResolver(), getDeckUri(), getCardsPager().getCurrentItem());
	}
}
