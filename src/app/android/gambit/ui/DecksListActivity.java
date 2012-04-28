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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.gambit.R;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import app.android.gambit.local.DbException;


public class DecksListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decks);

		initializeActionbar();
		initializeList();
	}

	private void initializeActionbar() {
		ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateListener);

		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		itemCreationButton.setOnClickListener(deckCreationListener);
	}

	private final OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO: Check sync document name existing
			// if false — call sync setup, true — call update
			callDecksUpdating();
		}

		private void callDecksUpdating() {
			new UpdateDecksTask().execute();
		}
	};

	private class UpdateDecksTask extends AsyncTask<Void, Void, String>
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

	private final OnClickListener deckCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callDeckCreation();
		}

		private void callDeckCreation() {
			Intent callIntent = IntentFactory.createDeckCreationIntent(activityContext);
			activityContext.startActivity(callIntent);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter decksAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.decks_list_item, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

		setListAdapter(decksAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		registerForContextMenu(getListView());
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadDecks();
	}

	private void loadDecks() {
		new LoadDecksTask().execute();
	}

	private class LoadDecksTask extends AsyncTask<Void, Void, String>
	{
		private List<Deck> decks;

		@Override
		protected void onPreExecute() {
			setEmptyListText(getString(R.string.loadingDecks));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				decks = DbProvider.getInstance().getDecks().getDecksList();
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (decks.isEmpty()) {
				setEmptyListText(getString(R.string.noDecks));
			}
			else {
				fillList(decks);
				updateList();
			}

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Deck deck = (Deck) itemData;

		HashMap<String, Object> deckItem = new HashMap<String, Object>();

		deckItem.put(LIST_ITEM_TEXT_ID, deck.getTitle());
		deckItem.put(LIST_ITEM_OBJECT_ID, deck);

		listData.add(deckItem);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		getMenuInflater().inflate(R.menu.decks_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int deckPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.rename:
				callDeckEditing(deckPosition);
				return true;

			case R.id.editCards:
				callCardsEditing(deckPosition);
				return true;

			case R.id.delete:
				callDeckDeleting(deckPosition);
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callDeckEditing(int deckPosition) {
		Deck deck = getDeck(deckPosition);

		Intent callIntent = IntentFactory.createDeckEditingIntent(activityContext, deck);
		startActivity(callIntent);
	}

	private Deck getDeck(int deckPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(deckPosition);

		return (Deck) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	private void callCardsEditing(int deckPosition) {
		Deck deck = getDeck(deckPosition);

		Intent callIntent = IntentFactory.createCardsListIntent(activityContext, deck);
		startActivity(callIntent);
	}

	private void callDeckDeleting(int deckPosition) {
		new DeleteDeckTask(deckPosition).execute();
	}

	private class DeleteDeckTask extends AsyncTask<Void, Void, String>
	{
		private final int deckPosition;
		private final Deck deck;

		public DeleteDeckTask(int deckPosition) {
			super();

			this.deckPosition = deckPosition;
			this.deck = getDeck(deckPosition);
		}

		@Override
		protected void onPreExecute() {
			listData.remove(deckPosition);
			updateList();

			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noDecks));
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				DbProvider.getInstance().getDecks().deleteDeck(deck);
			}
			catch (DbException e) {
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

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		callDeckViewing(position);
	}

	private void callDeckViewing(int deckPosition) {
		new ViewDeckTask(deckPosition).execute();
	}

	private class ViewDeckTask extends AsyncTask<Void, Void, String>
	{
		private final Deck deck;

		public ViewDeckTask(int deckPosition) {
			this.deck = getDeck(deckPosition);
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				if (deck.isEmpty()) {
					return getString(R.string.noCards);
				}
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (errorMessage.isEmpty()) {
				callCardsViewing(deck);
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}

		private void callCardsViewing(Deck deck) {
			Intent callIntent = IntentFactory.createCardsViewingIntent(activityContext, deck);
			startActivity(callIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.decks_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				callSettings();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void callSettings() {
		Intent callIntent = IntentFactory.createSettingsIntent(activityContext);
		startActivity(callIntent);
	}
}
