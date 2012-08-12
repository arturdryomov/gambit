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
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.Card;
import ru.ming13.gambit.local.Deck;
import com.actionbarsherlock.view.Menu;
import ru.ming13.gambit.ui.IntentCorruptedException;
import ru.ming13.gambit.ui.IntentFactory;
import ru.ming13.gambit.ui.IntentProcessor;
import ru.ming13.gambit.ui.UserAlerter;


public class CardsListActivity extends AdaptedListActivity
{
	private Deck deck;

	private static final String LIST_ITEM_FRONT_TEXT_ID = "front_text";
	private static final String LIST_ITEM_BACK_TEXT_ID = "back_text";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		processReceivedDeck();

		setUpCardsContextMenu();
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		String[] listColumnNames = {LIST_ITEM_FRONT_TEXT_ID, LIST_ITEM_BACK_TEXT_ID};
		int[] listColumnCorrespondingResources = {R.id.text_first_line, R.id.test_second_line};

		return new SimpleAdapter(this, list, R.layout.list_item_two_line, listColumnNames,
			listColumnCorrespondingResources);
	}

	@Override
	protected Map<String, Object> buildListItem(Object itemObject) {
		Card card = (Card) itemObject;

		HashMap<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, card);
		listItem.put(LIST_ITEM_FRONT_TEXT_ID, card.getFrontSideText());
		listItem.put(LIST_ITEM_BACK_TEXT_ID, card.getBackSideText());

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		new LoadCardsTask().execute();
	}

	private class LoadCardsTask extends AsyncTask<Void, Void, Void>
	{
		private List<Card> cards;

		@Override
		protected void onPreExecute() {
			setEmptyListText(R.string.loading_cards);
		}

		@Override
		protected Void doInBackground(Void... params) {
			cards = deck.getCardsList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (cards.isEmpty()) {
				setEmptyListText(R.string.empty_cards);
			}
			else {
				populateList(cards);
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
			actionMode.getMenuInflater().inflate(R.menu.menu_context_cards, menu);
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
			case R.id.menu_edit:
				callCardEditing(selectedItemPosition);
				return true;

			case R.id.menu_delete:
				callCardDeleting(selectedItemPosition);
				return true;

			default:
				return false;
		}
	}

	private void callCardEditing(int cardPosition) {
		Card card = getCard(cardPosition);

		Intent callIntent = IntentFactory.createCardEditingIntent(this, card);
		startActivity(callIntent);
	}

	private Card getCard(int cardPosition) {
		return (Card) getListItemObject(cardPosition);
	}

	private void callCardDeleting(int cardPosition) {
		new DeleteCardTask(cardPosition).execute();
	}

	private class DeleteCardTask extends AsyncTask<Void, Void, Void>
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
			list.remove(cardPosition);
			refreshListContent();

			if (list.isEmpty()) {
				setEmptyListText(R.string.empty_cards);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			deck.deleteCard(card);

			return null;
		}
	}

	private void processReceivedDeck() {
		try {
			deck = (Deck) IntentProcessor.getMessage(this);
		}
		catch (IntentCorruptedException e) {
			UserAlerter.alert(this, R.string.error_unspecified);

			finish();
		}
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		callCardEditing(position);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		getMenuInflater().inflate(R.menu.menu_context_cards, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int cardPosition = itemInfo.position;

		return handleContextMenuItem(item, cardPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_cards, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_create_item:
				callCardCreation();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void callCardCreation() {
		Intent callIntent = IntentFactory.createCardCreationIntent(this, deck);
		startActivity(callIntent);
	}
}
