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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.DbProvider;
import ru.ming13.gambit.local.Deck;
import com.actionbarsherlock.view.Menu;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.util.SynchronizationTask;
import ru.ming13.gambit.ui.util.UserAlerter;


public class DecksListActivity extends AdaptedListActivity
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// This is main activity that sets application launcher label. If title will be set in
		//   AndroidManifest.xml then launcher sign will be not correct.
		setUpActivityTitle();

		setUpCardsContextMenu();
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		String[] listColumnNames = {LIST_ITEM_TEXT_ID};
		int[] listColumnCorrespondingResources = {R.id.text};

		return new SimpleAdapter(this, list, R.layout.list_item_one_line, listColumnNames,
			listColumnCorrespondingResources);
	}

	@Override
	protected Map<String, Object> buildListItem(Object itemObject) {
		Deck deck = (Deck) itemObject;

		HashMap<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_TEXT_ID, deck.getTitle());
		listItem.put(LIST_ITEM_OBJECT_ID, deck);

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		new LoadDecksTask().execute();
	}

	private class LoadDecksTask extends AsyncTask<Void, Void, Void>
	{
		private List<Deck> decks;

		@Override
		protected void onPreExecute() {
			setEmptyListText(R.string.loading_decks);
		}

		@Override
		protected Void doInBackground(Void... params) {
			decks = DbProvider.getInstance().getDecks().getDecksList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (decks.isEmpty()) {
				setEmptyListText(R.string.empty_decks);
			}
			else {
				populateList(decks);
			}
		}
	}

	private void setUpCardsContextMenu() {
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

		Intent callIntent = IntentFactory.createDeckEditingIntent(this, deck);
		startActivity(callIntent);
	}

	private Deck getDeck(int deckPosition) {
		return (Deck) getListItemObject(deckPosition);
	}

	private void callCardsEditing(int deckPosition) {
		Deck deck = getDeck(deckPosition);

		Intent callIntent = IntentFactory.createCardsEditingIntent(this, deck);
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
			list.remove(deckPosition);
			refreshListContent();

			if (list.isEmpty()) {
				setEmptyListText(R.string.empty_decks);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			DbProvider.getInstance().getDecks().deleteDeck(deck);

			return null;
		}
	}

	private void setUpActivityTitle() {
		getSupportActionBar().setTitle(R.string.title_decks);
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
			if (TextUtils.isEmpty(errorMessage)) {
				callCardsViewing(deck);
			}
			else {
				UserAlerter.alert(DecksListActivity.this, errorMessage);
			}
		}
	}

	private void callCardsViewing(Deck deck) {
		Intent callIntent = IntentFactory.createCardsViewingIntent(this, deck);
		startActivity(callIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_decks, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_create_item:
				callDeckCreation();
				return true;

			case R.id.menu_sync:
				callSyncing();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void callDeckCreation() {
		Intent callIntent = IntentFactory.createDeckCreationIntent(this);
		startActivity(callIntent);
	}

	private void callSyncing() {
		Runnable successRunnable = new Runnable()
		{
			@Override
			public void run() {
				callListPopulation();
			}
		};

		SynchronizationTask synchronizationTask = new SynchronizationTask(this, successRunnable);
		synchronizationTask.execute();
	}
}
