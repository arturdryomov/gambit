package app.android.gambit.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.gambit.R;
import app.android.gambit.models.Card;
import app.android.gambit.models.Deck;
import app.android.gambit.models.ModelsException;


public class CardsListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private Deck deck;

	private static final String LIST_ITEM_FRONT_TEXT_ID = "front_text";
	private static final String LIST_ITEM_BACK_TEXT_ID = "back_text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);

		initializeActionbar();
		initializeList();

		processReceivedDeck();
	}

	private void initializeActionbar() {
		ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateListener);

		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		itemCreationButton.setOnClickListener(cardCreationListener);
	}

	private final OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callCardsUpdating();
		}

		private void callCardsUpdating() {
			new UpdateCardsTask().execute();
		}
	};

	private class UpdateCardsTask extends AsyncTask<Void, Void, String>
	{
		// Just obtain authorization token at moment

		@Override
		protected String doInBackground(Void... params) {
			try {
				Activity activity = (Activity) activityContext;

				Account account = AccountSelector.select(activity);

				Authorizer authorizer = new Authorizer(activity);
				String authToken = authorizer.getToken(Authorizer.ServiceType.SPREADSHEETS, account);

				return String.format("Token received: '%s'.", authToken);
			}
			catch (NoAccountRegisteredException e) {
				return getString(R.string.noGoogleAccounts);
			}
			// TODO: Remove this exception as useless
			catch (AuthorizationCanceledException e) {
				return getString(R.string.authenticationCanceled);
			}
			catch (AuthorizationFailedException e) {
				return getString(R.string.authenticationError);
			}
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private final OnClickListener cardCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callCardCreation();
		}

		private void callCardCreation() {
			Intent callIntent = IntentFactory.createCardCreationIntent(activityContext, deck);
			startActivity(callIntent);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter cardsAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.cards_list_item, new String[] { LIST_ITEM_FRONT_TEXT_ID, LIST_ITEM_BACK_TEXT_ID },
			new int[] { R.id.title, R.id.description });

		setListAdapter(cardsAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		registerForContextMenu(getListView());
	}

	private void processReceivedDeck() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			deck = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.someError));

			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadCards();
	}

	private void loadCards() {
		new LoadCardsTask().execute();
	}

	private class LoadCardsTask extends AsyncTask<Void, Void, String>
	{
		private List<Card> cards;

		@Override
		protected void onPreExecute() {
			setEmptyListText(getString(R.string.loadingCards));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				cards = deck.getCardsList();
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (cards.isEmpty()) {
				setEmptyListText(getString(R.string.noCards));
			}
			else {
				fillList(cards);
				updateList();
			}

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Card card = (Card) itemData;

		HashMap<String, Object> cardItem = new HashMap<String, Object>();

		cardItem.put(LIST_ITEM_FRONT_TEXT_ID, card.getFrontSideText());
		cardItem.put(LIST_ITEM_BACK_TEXT_ID, card.getBackSideText());
		cardItem.put(LIST_ITEM_OBJECT_ID, card);

		listData.add(cardItem);
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		callCardEditing(position);
	}

	private void callCardEditing(int cardPosition) {
		Card card = getCard(cardPosition);

		Intent callIntent = IntentFactory.createCardEditingIntent(activityContext, card);
		startActivity(callIntent);
	}

	private Card getCard(int cardPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(cardPosition);

		return (Card) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		getMenuInflater().inflate(R.menu.cards_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int cardPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.delete:
				callCardDeleting(cardPosition);
				return true;

			case R.id.edit:
				callCardEditing(cardPosition);
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callCardDeleting(int cardPosition) {
		new DeleteCardTask(cardPosition).execute();
	}

	private class DeleteCardTask extends AsyncTask<Void, Void, String>
	{
		private final int cardPosition;
		private final Card card;

		public DeleteCardTask(int cardPosition) {
			super();

			this.cardPosition = cardPosition;
			this.card = getCard(cardPosition);
		}

		@Override
		protected void onPreExecute() {
			listData.remove(cardPosition);
			updateList();

			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noCards));
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				deck.deleteCard(card);
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
}
