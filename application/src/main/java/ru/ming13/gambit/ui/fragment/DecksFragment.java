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

package ru.ming13.gambit.ui.fragment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.loader.DeckOperationLoader;
import ru.ming13.gambit.ui.loader.DecksLoader;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.loader.result.LoaderResult;


public class DecksFragment extends AdaptedListFragment<Deck>
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	public static DecksFragment newInstance() {
		return new DecksFragment();
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		String[] listColumnNames = {LIST_ITEM_TEXT_ID};
		int[] listColumnCorrespondingResources = {R.id.text};

		return new SimpleAdapter(getActivity(), list, R.layout.list_item_one_line, listColumnNames,
			listColumnCorrespondingResources);
	}

	@Override
	protected Map<String, Object> buildListItem(Deck deck) {
		HashMap<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_TEXT_ID, deck.getTitle());
		listItem.put(LIST_ITEM_OBJECT_ID, deck);

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		DecksLoaderCallback decksLoaderCallback = new DecksLoaderCallback(this);

		getLoaderManager().initLoader(Loaders.DECKS, null, decksLoaderCallback);
	}

	private static class DecksLoaderCallback implements LoaderManager.LoaderCallbacks<LoaderResult<List<Deck>>>
	{
		private final DecksFragment decksFragment;

		public DecksLoaderCallback(DecksFragment decksFragment) {
			this.decksFragment = decksFragment;
		}

		@Override
		public Loader<LoaderResult<List<Deck>>> onCreateLoader(int loaderId, Bundle loaderArguments) {
			decksFragment.setEmptyListText(R.string.loading_decks);

			return DecksLoader.newInstance(decksFragment.getActivity());
		}

		@Override
		public void onLoadFinished(Loader<LoaderResult<List<Deck>>> decksLoader, LoaderResult<List<Deck>> decksLoaderResult) {
			List<Deck> decks = decksLoaderResult.getData();

			if (decks.isEmpty()) {
				decksFragment.setEmptyListText(R.string.empty_decks);
			}
			else {
				decksFragment.populateList(decks);
			}
		}

		@Override
		public void onLoaderReset(Loader<LoaderResult<List<Deck>>> decksLoader) {
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpContextMenu();
	}

	private void setUpContextMenu() {
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
		super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

		getActivity().getMenuInflater().inflate(R.menu.menu_context_decks, contextMenu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		int deckListPosition = getListPosition(menuItem);
		Deck deck = getListItemObject(deckListPosition);

		switch (menuItem.getItemId()) {
			case R.id.menu_rename:
				callDeckRenaming(deck);
				return true;

			case R.id.menu_edit_cards:
				callCardsEditing(deck);
				return true;

			case R.id.menu_delete:
				callDeckDeletion(deck, deckListPosition);
				return true;

			default:
				return false;
		}
	}

	private void callDeckRenaming(Deck deck) {
		Intent intent = IntentFactory.createDeckRenamingIntent(getActivity(), deck);

		startActivity(intent);
	}

	private void callCardsEditing(Deck deck) {
		Intent intent = IntentFactory.createCardsListIntent(getActivity(), deck);

		startActivity(intent);
	}

	private void callDeckDeletion(Deck deck, int deckListPosition) {
		deleteDeckFromList(deckListPosition);
		deleteDeckEntirely(deck);
	}

	private void deleteDeckFromList(int deckListPosition) {
		list.remove(deckListPosition);
		refreshListContent();

		if (list.isEmpty()) {
			setEmptyListText(R.string.empty_decks);
		}
	}

	private void deleteDeckEntirely(Deck deck) {
		DeckDeletionLoaderCallback deckDeletionLoaderCallback = new DeckDeletionLoaderCallback(
			getActivity(), deck);

		getLoaderManager().initLoader(Loaders.DECK_OPERATION, null, deckDeletionLoaderCallback);
	}

	private static class DeckDeletionLoaderCallback implements LoaderManager.LoaderCallbacks<LoaderResult<Deck>>
	{
		private final Context context;

		private final Deck deck;

		public DeckDeletionLoaderCallback(Context context, Deck deck) {
			this.context = context;

			this.deck = deck;
		}

		@Override
		public Loader<LoaderResult<Deck>> onCreateLoader(int loaderId, Bundle loaderArguments) {
			return DeckOperationLoader.newDeletionLoader(context, deck);
		}

		@Override
		public void onLoadFinished(Loader<LoaderResult<Deck>> deckOperationLoader, LoaderResult<Deck> deckOperationLoaderResult) {
		}

		@Override
		public void onLoaderReset(Loader<LoaderResult<Deck>> deckOperationLoader) {
		}
	}

	@Override
	public void onListItemClick(ListView listView, View view, int listPosition, long rowId) {
		Deck deck = getListItemObject(listPosition);

		callCardsViewing(deck);
	}

	private void callCardsViewing(Deck deck) {
		Intent intent = IntentFactory.createCardsViewingIntent(getActivity(), deck);

		startActivity(intent);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_action_bar_decks, menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_create_item:
					callDeckCreation();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callDeckCreation() {
		Intent intent = IntentFactory.createDeckCreationIntent(getActivity());

		startActivity(intent);
	}
}
