package app.android.simpleflashcards.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import app.android.simpleflashcards.R;
import app.android.simpleflashcards.SimpleFlashcardsApplication;
import app.android.simpleflashcards.models.Card;
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.ModelsException;


public class CardsViewingActivity extends Activity
{
	private final Context activityContext = this;

	private final List<HashMap<String, Object>> cardsData;

	private static enum CardSide {
		FRONT, BACK
	}

	private static enum CardsOrder {
		DEFAULT, STRAIGHT, SHUFFLE
	}

	private static final String CARDS_DATA_BACK_SIDE_TEXT_ID = "back_side";
	private static final String CARDS_DATA_FRONT_SIDE_TEXT_ID = "front_side";
	private static final String CARDS_DATA_CURRENT_SIDE_ID = "current_side";

	private int deckId;

	public CardsViewingActivity() {
		super();

		cardsData = new ArrayList<HashMap<String, Object>>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards_viewing);

		processActivityMessage();

		new LoadCardsTask(CardsOrder.DEFAULT).execute();
	}

	private void processActivityMessage() {
		deckId = ActivityMessager.getMessageFromActivity(this);
	}

	private class LoadCardsTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		private final CardsOrder cardsOrder;

		public LoadCardsTask(CardsOrder cardsOrder) {
			this.cardsOrder = cardsOrder;
		}

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.loadingCards));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Deck deck = SimpleFlashcardsApplication.getInstance().getDecks().getDeckById(deckId);

				switch (cardsOrder) {
					case SHUFFLE:
						deck.shuffleCards();
						break;

					case STRAIGHT:
						deck.resetCardsOrder();
						break;

					case DEFAULT:
						break;
				}

				fillList(deck.getCardsList());
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			progressDialogHelper.hide();

			if (errorMessage.isEmpty()) {
				initializeCardsAdapter();

				if (cardsOrder == CardsOrder.DEFAULT) {
					new RestoreCardsPosition().execute();
				}
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private void fillList(List<Card> cards) {
		cardsData.clear();

		for (Card card : cards) {
			addCardToList(card);
		}
	}

	private void addCardToList(Card card) {
		HashMap<String, Object> cardItem = new HashMap<String, Object>();

		cardItem.put(CARDS_DATA_FRONT_SIDE_TEXT_ID, card.getFrontSideText());
		cardItem.put(CARDS_DATA_BACK_SIDE_TEXT_ID, card.getBackSideText());
		cardItem.put(CARDS_DATA_CURRENT_SIDE_ID, CardSide.FRONT);

		cardsData.add(cardItem);
	}

	private void initializeCardsAdapter() {
		CardsAdapter cardsAdapter = new CardsAdapter();

		ViewPager cardsPager = (ViewPager) findViewById(R.id.cardsPager);

		cardsPager.setAdapter(cardsAdapter);
	}

	private class CardsAdapter extends PagerAdapter
	{
		@Override
		public int getCount() {
			return cardsData.size();
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			TextView textView = new TextView(activityContext);

			textView.setText(getCardText(position));
			textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			textView.setTextSize(30);

			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView textView = (TextView) v;

					invertCardSide(position);

					textView.setText(getCardText(position));
				}
			});

			((ViewPager) container).addView(textView, 0);

			return textView;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((TextView) object);

			setDefaultCardSide(position);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (TextView) object;
		}
	}

	private String getCardText(int position) {
		HashMap<String, Object> cardItem = cardsData.get(position);

		CardSide cardSide = (CardSide) cardItem.get(CARDS_DATA_CURRENT_SIDE_ID);

		switch (cardSide) {
			case FRONT:
				return (String) cardItem.get(CARDS_DATA_FRONT_SIDE_TEXT_ID);

			case BACK:
				return (String) cardItem.get(CARDS_DATA_BACK_SIDE_TEXT_ID);

			default:
				return new String();
		}
	}

	private void invertCardSide(int position) {
		HashMap<String, Object> cardItem = cardsData.get(position);

		CardSide cardSide = (CardSide) cardItem.get(CARDS_DATA_CURRENT_SIDE_ID);
		CardSide invertedCardSide;

		switch (cardSide) {
			case FRONT:
				invertedCardSide = CardSide.BACK;
				break;

			case BACK:
				invertedCardSide = CardSide.FRONT;
				break;

			default:
				invertedCardSide = CardSide.FRONT;
				break;
		}

		setCardSide(invertedCardSide, position);
	}

	private void setDefaultCardSide(int position) {
		setCardSide(CardSide.FRONT, position);
	}

	private void setCardSide(CardSide cardSide, int position) {
		HashMap<String, Object> cardItem = cardsData.get(position);

		cardItem.put(CARDS_DATA_CURRENT_SIDE_ID, cardSide);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cards_viewing_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.shuffle:
				new LoadCardsTask(CardsOrder.SHUFFLE).execute();
				return true;

			case R.id.reset:
				new LoadCardsTask(CardsOrder.STRAIGHT).execute();
				return true;

			case R.id.back:
				goToFirstCard();

				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void goToFirstCard() {
		setCardsPagerPosition(0);
	}

	private class RestoreCardsPosition extends AsyncTask<Void, Void, String>
	{
		private int currentCardPosition;

		@Override
		protected String doInBackground(Void... params) {
			try {
				currentCardPosition = SimpleFlashcardsApplication.getInstance().getDecks()
					.getDeckById(deckId).getCurrentCardIndex();
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (errorMessage.isEmpty()) {
				if (currentCardPosition < cardsData.size()) {
					setCardsPagerPosition(currentCardPosition);
				}
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private void setCardsPagerPosition(int position) {
		ViewPager cardsPager = (ViewPager) findViewById(R.id.cardsPager);

		cardsPager.setCurrentItem(position);
	}

	@Override
	protected void onPause() {
		super.onPause();

		new StoreCardsPosition().execute();
	}

	private class StoreCardsPosition extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				SimpleFlashcardsApplication.getInstance().getDecks().getDeckById(deckId)
					.setCurrentCardIndex(getCardsPagerPosition());
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private int getCardsPagerPosition() {
		ViewPager cardsPager = (ViewPager) findViewById(R.id.cardsPager);

		return cardsPager.getCurrentItem();
	}
}
