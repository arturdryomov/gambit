package app.android.gambit.ui;


import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.gambit.R;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import com.actionbarsherlock.view.Menu;


public class DecksListActivity extends SimpleAdapterListActivity
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_list);
		super.onCreate(savedInstanceState);

		// This is main activity that sets application launcher label. If title will be set in
		//   AndroidManifest.xml then launcher sign will be not correct.
		setTitle();
	}

	@Override
	protected void initializeList() {
		SimpleAdapter decksAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.list_item_one_line, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

		setListAdapter(decksAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getListView().setOnItemLongClickListener(actionModeListener);
		}
		else {
			registerForContextMenu(getListView());
		}
	}

	private final AdapterView.OnItemLongClickListener actionModeListener = new AdapterView.OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
			startActionMode(new ActionModeCallback(position));

			return true;
		}
	};

	private class ActionModeCallback implements ActionMode.Callback
	{
		private final int selectedItemPosition;

		public ActionModeCallback(int selectedItemPosition) {
			this.selectedItemPosition = selectedItemPosition;
		}

		@Override
		public boolean onCreateActionMode(ActionMode actionMode, android.view.Menu menu) {
			actionMode.getMenuInflater().inflate(R.menu.menu_context_decks, menu);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, android.view.Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
			if (handleContextMenuItem(menuItem, selectedItemPosition)) {
				actionMode.finish();

				return true;
			}

			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
		}
	}

	private boolean handleContextMenuItem(MenuItem menuItem, int selectedItemPosition) {
		switch (menuItem.getItemId()) {
			case R.id.menu_rename:
				callDeckRenaming(selectedItemPosition);
				return true;

			case R.id.menu_edit_cards:
				callCardsEditing(selectedItemPosition);
				return true;

			case R.id.menu_delete:
				callDeckDeleting(selectedItemPosition);
				return true;

			default:
				return false;
		}
	}

	private void callDeckRenaming(int deckPosition) {
		Deck deck = getDeck(deckPosition);

		Intent callIntent = IntentFactory.createDeckEditingIntent(activityContext, deck);
		startActivity(callIntent);
	}

	private Deck getDeck(int deckPosition) {
		return (Deck) getObject(deckPosition);
	}

	private void callCardsEditing(int deckPosition) {
		Deck deck = getDeck(deckPosition);

		Intent callIntent = IntentFactory.createCardsEditingIntent(activityContext, deck);
		startActivity(callIntent);
	}

	private void callDeckDeleting(int deckPosition) {
		new DeleteDeckTask(deckPosition).execute();
	}

	private class DeleteDeckTask extends AsyncTask<Void, Void, Void>
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
				setEmptyListText(getString(R.string.empty_decks));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			DbProvider.getInstance().getDecks().deleteDeck(deck);

			return null;
		}
	}

	private void setTitle() {
		getSupportActionBar().setTitle(R.string.title_decks);
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadDecks();
	}

	private void loadDecks() {
		new LoadDecksTask().execute();
	}

	private class LoadDecksTask extends AsyncTask<Void, Void, Void>
	{
		private List<Deck> decks;

		@Override
		protected void onPreExecute() {
			setEmptyListText(getString(R.string.loading_decks));
		}

		@Override
		protected Void doInBackground(Void... params) {
			decks = DbProvider.getInstance().getDecks().getDecksList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (decks.isEmpty()) {
				setEmptyListText(getString(R.string.empty_decks));
			}
			else {
				fillList(decks);
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
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		getMenuInflater().inflate(R.menu.menu_context_decks, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int deckPosition = itemInfo.position;

		return handleContextMenuItem(item, deckPosition);
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		callCardsViewing(position);
	}

	private void callCardsViewing(int deckPosition) {
		new ViewCardsTask(deckPosition).execute();
	}

	private class ViewCardsTask extends AsyncTask<Void, Void, String>
	{
		private final Deck deck;

		public ViewCardsTask(int deckPosition) {
			this.deck = getDeck(deckPosition);
		}

		@Override
		protected String doInBackground(Void... params) {
			if (deck.isEmpty()) {
				return getString(R.string.empty_cards);
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
	}

	private void callCardsViewing(Deck deck) {
		Intent callIntent = IntentFactory.createCardsViewingIntent(activityContext, deck);
		startActivity(callIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_decks_and_cards, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_create_item:
				callDeckCreation();
				return true;

			case R.id.menu_sync:
				// TODO: Call decks updating
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void callDeckCreation() {
		Intent callIntent = IntentFactory.createDeckCreationIntent(activityContext);
		activityContext.startActivity(callIntent);
	}
}
