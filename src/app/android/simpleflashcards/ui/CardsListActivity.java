package app.android.simpleflashcards.ui;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.simpleflashcards.R;
import app.android.simpleflashcards.models.Card;
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.ModelsException;


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

		ImageButton newItemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		newItemCreationButton.setOnClickListener(cardCreationListener);
	}

	private final OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO: Call update task
		}
	};

	private final OnClickListener cardCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callCardCreation();
		}
	};

	private void callCardCreation() {
		Intent callIntent = IntentFactory.createCardCreationIntent(activityContext, deck);
		startActivity(callIntent);
	}

	@Override
	protected void initializeList() {
		SimpleAdapter flashcardsAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.cards_list_item, new String[] { LIST_ITEM_FRONT_TEXT_ID, LIST_ITEM_BACK_TEXT_ID },
			new int[] { R.id.title, R.id.description });

		setListAdapter(flashcardsAdapter);

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

		new LoadCardsTask().execute();
	}

	private class LoadCardsTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() {
			setEmptyListText(getString(R.string.loadingCards));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				fillList(deck.getCardsList());
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noCards));
			}
			else {
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		callCardEditing(position);
	}

	private void callCardEditing(int cardPosition) {
		Card card = getCard(cardPosition);

		Intent callIntent = IntentFactory.createCardEditingIntent(activityContext, card);
		startActivity(callIntent);
	}

	private Card getCard(int adapterPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(adapterPosition);

		return (Card) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.cards_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int itemPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.delete:
				new DeleteCardTask(itemPosition).execute();
				return true;

			default:
				return super.onContextItemSelected(item);
		}
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
			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noCards));
			}

			updateList();

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}
}
