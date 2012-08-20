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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.model.Card;
import ru.ming13.gambit.local.model.Deck;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.loader.CardOperationLoader;
import ru.ming13.gambit.ui.loader.CardsLoader;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.loader.result.LoaderResult;


public class CardsFragment extends AdaptedListFragment<Card>
{
	private static final String LIST_ITEM_FRONT_TEXT_ID = "front_text";
	private static final String LIST_ITEM_BACK_TEXT_ID = "back_text";

	private Deck deck;

	public static CardsFragment newInstance(Deck deck) {
		CardsFragment cardsFragment = new CardsFragment();

		cardsFragment.setArguments(buildArguments(deck));

		return cardsFragment;
	}

	private static Bundle buildArguments(Deck deck) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(FragmentArguments.DECK, deck);

		return bundle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		deck = getArguments().getParcelable(FragmentArguments.DECK);
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		String[] listColumnNames = {LIST_ITEM_FRONT_TEXT_ID, LIST_ITEM_BACK_TEXT_ID};
		int[] listColumnCorrespondingResources = {R.id.text_first_line, R.id.text_second_line};

		return new SimpleAdapter(getActivity(), list, R.layout.list_item_two_line, listColumnNames,
			listColumnCorrespondingResources);
	}

	@Override
	protected Map<String, Object> buildListItem(Card card) {
		HashMap<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, card);
		listItem.put(LIST_ITEM_FRONT_TEXT_ID, card.getFrontSideText());
		listItem.put(LIST_ITEM_BACK_TEXT_ID, card.getBackSideText());

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		CardsLoaderCallback cardsLoaderCallback = new CardsLoaderCallback(this);

		getLoaderManager().initLoader(Loaders.CARDS, null, cardsLoaderCallback);
	}

	private static class CardsLoaderCallback implements LoaderManager.LoaderCallbacks<LoaderResult<List<Card>>>
	{
		private final CardsFragment cardsFragment;

		public CardsLoaderCallback(CardsFragment cardsFragment) {
			this.cardsFragment = cardsFragment;
		}

		@Override
		public Loader<LoaderResult<List<Card>>> onCreateLoader(int loaderId, Bundle loaderArguments) {
			cardsFragment.setEmptyListText(R.string.loading_cards);

			return CardsLoader.newCurrentOrderInstance(cardsFragment.getActivity(), cardsFragment.deck);
		}

		@Override
		public void onLoadFinished(Loader<LoaderResult<List<Card>>> cardsLoader, LoaderResult<List<Card>> cardsLoaderResult) {
			List<Card> cards = cardsLoaderResult.getData();

			if (cards.isEmpty()) {
				cardsFragment.setEmptyListText(R.string.empty_cards);
			}
			else {
				cardsFragment.populateList(cards);
			}
		}

		@Override
		public void onLoaderReset(Loader<LoaderResult<List<Card>>> cardsLoader) {
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpContextMenu();
	}

	private void setUpContextMenu() {
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
			getListView().startActionMode(new ActionModeCallback(CardsFragment.this, position));

			return true;
		}
	};

	private static class ActionModeCallback implements ActionMode.Callback
	{
		private final CardsFragment cardsFragment;

		private final int cardListPosition;

		public ActionModeCallback(CardsFragment cardsFragment, int cardListPosition) {
			this.cardsFragment = cardsFragment;

			this.cardListPosition = cardListPosition;
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
			if (cardsFragment.handleContextMenu(menuItem, cardListPosition)) {
				actionMode.finish();

				return true;
			}

			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
		}
	}

	private boolean handleContextMenu(MenuItem menuItem, int cardListPosition) {
		switch (menuItem.getItemId()) {
			case R.id.menu_edit:
				callCardModification(cardListPosition);
				return true;

			case R.id.menu_delete:
				callCardDeletion(cardListPosition);
				return true;

			default:
				return false;
		}
	}

	private void callCardModification(int cardListPosition) {
		Card card = getListItemObject(cardListPosition);

		Intent intent = IntentFactory.createCardModificationIntent(getActivity(), card);

		startActivity(intent);
	}

	private void callCardDeletion(int cardListPosition) {
		Card card = getListItemObject(cardListPosition);

		deleteCardFromList(cardListPosition);
		deleteCardEntirely(card);
	}

	private void deleteCardFromList(int cardListPosition) {
		list.remove(cardListPosition);
		refreshListContent();

		if (list.isEmpty()) {
			setEmptyListText(R.string.empty_cards);
		}
	}

	private void deleteCardEntirely(Card card) {
		CardDeletionLoaderCallback cardDeletionLoaderCallback = new CardDeletionLoaderCallback(
			getActivity(), deck, card);

		getLoaderManager().restartLoader(Loaders.CARD_OPERATION, null, cardDeletionLoaderCallback);
	}

	private static class CardDeletionLoaderCallback implements LoaderManager.LoaderCallbacks<LoaderResult<Card>>
	{
		private final Context context;

		private final Deck deck;
		private final Card card;

		public CardDeletionLoaderCallback(Context context, Deck deck, Card card) {
			this.context = context;

			this.deck = deck;
			this.card = card;
		}

		@Override
		public Loader<LoaderResult<Card>> onCreateLoader(int loaderId, Bundle loaderArguments) {
			return CardOperationLoader.newDeletionInstance(context, deck, card);
		}

		@Override
		public void onLoadFinished(Loader<LoaderResult<Card>> cardOperationLoader, LoaderResult<Card> cardOperationLoaderResult) {
		}

		@Override
		public void onLoaderReset(Loader<LoaderResult<Card>> cardOperationLoader) {
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
		super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

		getActivity().getMenuInflater().inflate(R.menu.menu_context_cards, contextMenu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		int cardListPosition = getListPosition(menuItem);

		return handleContextMenu(menuItem, cardListPosition);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int listPosition, long rowId) {
		callCardModification(listPosition);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_action_bar_cards, menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_create_item:
				callCardCreation();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callCardCreation() {
		Intent intent = IntentFactory.createCardCreationIntent(getActivity(), deck);

		startActivity(intent);
	}
}
