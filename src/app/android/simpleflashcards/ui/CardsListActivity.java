package app.android.simpleflashcards.ui;


import java.util.HashMap;

import android.content.Context;
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
import app.android.simpleflashcards.SimpleFlashcardsApplication;
import app.android.simpleflashcards.models.Card;
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.ModelsException;


public class CardsListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_FRONT_TEXT = "front_text";
	private static final String LIST_ITEM_BACK_TEXT = "back_text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	private int deckId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);

		processActivityMessage();

		initializeActionbar();
		initializeList();
	}

	@Override
	protected void onResume() {
		super.onResume();

		new LoadCardsTask().execute();
	}

	private void processActivityMessage() {
		deckId = ActivityMessager.getMessageFromActivity(this);
	}

	private void initializeActionbar() {
		ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateListener);

		ImageButton newItemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		newItemCreationButton.setOnClickListener(flashcardCreationListener);
	}

	private OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO: Call update task
		}
	};

	private OnClickListener flashcardCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ActivityMessager
				.startActivityWithMessage(activityContext, CardCreationActivity.class, deckId);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter flashcardsAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.cards_list_item, new String[] { LIST_ITEM_FRONT_TEXT, LIST_ITEM_BACK_TEXT },
			new int[] { R.id.title, R.id.description });

		setListAdapter(flashcardsAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		registerForContextMenu(getListView());
	}

	private class LoadCardsTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() {
			setEmptyListText(getString(R.string.loadingCards));

			listData.clear();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Deck deck = SimpleFlashcardsApplication.getInstance().getDecks().getDeckById(deckId);
				addItemsToList(deck.getCardsList());
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noCards));
			}
			else {
				updateList();
			}

			if (!result.isEmpty()) {
				UserAlerter.alert(activityContext, result);
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Card card = (Card) itemData;

		HashMap<String, Object> cardItem = new HashMap<String, Object>();

		cardItem.put(LIST_ITEM_FRONT_TEXT, card.getFrontSideText());
		cardItem.put(LIST_ITEM_BACK_TEXT, card.getBackSideText());
		cardItem.put(LIST_ITEM_OBJECT_ID, card);

		listData.add(cardItem);
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
			case R.id.editItem:
				// TODO: Call edit task

				return true;

			case R.id.delete:
				// TODO: Call deletion task

				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}
}
