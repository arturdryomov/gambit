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

package ru.ming13.gambit.fragment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.transitions.everywhere.TransitionManager;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ming13.gambit.R;
import ru.ming13.gambit.adapter.DecksListAdapter;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckDeletedEvent;
import ru.ming13.gambit.bus.DeckSelectedEvent;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.DecksDeletionTask;
import ru.ming13.gambit.util.Android;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.ListUtil;
import ru.ming13.gambit.util.Loaders;

public class DecksListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ListView.MultiChoiceModeListener, ListView.OnItemLongClickListener
{
	public static DecksListFragment newInstance() {
		return new DecksListFragment();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpDecks();
	}

	private void setUpDecks() {
		setUpDecksList();
		setUpDecksAdapter();
		setUpDecksContent();
		setUpDecksActions();
	}

	private void setUpDecksList() {
		if (isTabletLayout()) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		} else {
			ListUtil.at(getListView()).switchChoiceModeToMultipleModal();
		}
	}

	private boolean isTabletLayout() {
		return Android.with(getActivity()).isTablet() && Android.with(getActivity()).isLandscape();
	}

	private void setUpDecksAdapter() {
		setListAdapter(new DecksListAdapter(getActivity()));
	}

	private void setUpDecksContent() {
		getLoaderManager().initLoader(Loaders.DECKS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		String sort = GambitContract.Decks.TITLE;

		return new CursorLoader(getActivity(), getDecksUri(), null, null, null, sort);
	}

	private Uri getDecksUri() {
		return GambitContract.Decks.getDecksUri();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> decksLoader, Cursor decksCursor) {
		setUpDecksAnimations();

		getDecksAdapter().swapCursor(decksCursor);

		setUpDecksMessage();
	}

	private void setUpDecksAnimations() {
		if (!getDecksAdapter().isEmpty()) {
			TransitionManager.beginDelayedTransition(getListView());
		}
	}

	private DecksListAdapter getDecksAdapter() {
		return (DecksListAdapter) getListAdapter();
	}

	private void setUpDecksMessage() {
		if (getDecksAdapter().isEmpty()) {
			showDecksMessage();
		} else {
			hideDecksMessage();
		}
	}

	private void showDecksMessage() {
		TextView messageTitleTextView = (TextView) getView().findViewById(R.id.text_message_title);
		TextView messageSummaryTextView = (TextView) getView().findViewById(R.id.text_message_summary);

		messageTitleTextView.setText(R.string.empty_decks_title);
		messageSummaryTextView.setText(R.string.empty_decks_subtitle);

		getView().findViewById(R.id.layout_message).setVisibility(View.VISIBLE);
	}

	private void hideDecksMessage() {
		getView().findViewById(R.id.layout_message).setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> decksLoader) {
		getDecksAdapter().swapCursor(null);
	}

	private void setUpDecksActions() {
		if (isTabletLayout()) {
			getListView().setOnItemLongClickListener(this);
		} else {
			getListView().setMultiChoiceModeListener(this);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> decksListView, View deckView, int deckPosition, long deckId) {
		getListView().clearChoices();
		getListView().setItemChecked(deckPosition, true);

		getListView().startActionMode(this);

		return true;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode actionMode, int deckPosition, long deckId, boolean deckChecked) {
		changeDecksActions(actionMode);
	}

	private void changeDecksActions(ActionMode actionMode) {
		MenuItem actionEditDeck = actionMode.getMenu().findItem(R.id.menu_edit);
		MenuItem actionEditCards = actionMode.getMenu().findItem(R.id.menu_edit_cards);

		actionEditDeck.setVisible(isSingleDeckSelected());
		actionEditCards.setVisible(isSingleDeckSelected());
	}

	private boolean isSingleDeckSelected() {
		return getListView().getCheckedItemCount() == 1;
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		actionMode.getMenuInflater().inflate(R.menu.action_mode_decks_list, menu);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		changeDecksActions(actionMode);

		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_edit:
				startDeckEditingActivity(getCheckedDeck());
				break;

			case R.id.menu_edit_cards:
				startCardsListActivity(getCheckedDeck());
				break;

			case R.id.menu_delete:
				startDecksDeletion(getCheckedDecks());
				break;

			default:
				return false;
		}

		actionMode.finish();

		return true;
	}

	private void startDeckEditingActivity(Deck deck) {
		Intent intent = Intents.Builder.with(getActivity()).buildDeckEditingIntent(deck);
		startActivity(intent);
	}

	private Deck getCheckedDeck() {
		return getCheckedDecks().get(0);
	}

	private List<Deck> getCheckedDecks() {
		List<Deck> decks = new ArrayList<Deck>();

		SparseBooleanArray checkedDecksPositions = getCheckedDecksPositions();

		for (int deckPosition = 0; deckPosition < checkedDecksPositions.size(); deckPosition++) {
			if (checkedDecksPositions.valueAt(deckPosition)) {
				decks.add(getDeck(checkedDecksPositions.keyAt(deckPosition)));
			}
		}

		return decks;
	}

	private SparseBooleanArray getCheckedDecksPositions() {
		return getListView().getCheckedItemPositions();
	}

	private Deck getDeck(int deckPosition) {
		Cursor decksCursor = getDecksCursor(deckPosition);

		long deckId = decksCursor.getLong(
			decksCursor.getColumnIndex(GambitContract.Decks._ID));
		String deckTitle = decksCursor.getString(
			decksCursor.getColumnIndex(GambitContract.Decks.TITLE));
		int currentCardPosition = decksCursor.getInt(
			decksCursor.getColumnIndex(GambitContract.Decks.CURRENT_CARD_INDEX));

		return new Deck(deckId, deckTitle, currentCardPosition);
	}

	private Cursor getDecksCursor(int deckPosition) {
		return (Cursor) getDecksAdapter().getItem(deckPosition);
	}

	private void startCardsListActivity(Deck deck) {
		Intent intent = Intents.Builder.with(getActivity()).buildCardsListIntent(deck);
		startActivity(intent);
	}

	private void startDecksDeletion(List<Deck> decks) {
		DecksDeletionTask.execute(getActivity().getContentResolver(), decks);

		if (decks.contains(getCheckedDeck())) {
			BusProvider.getBus().post(new DeckDeletedEvent());
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
	}

	@Override
	public void onListItemClick(ListView decksListView, View deckView, int deckPosition, long deckId) {
		BusProvider.getBus().post(new DeckSelectedEvent(getDeck(deckPosition)));
	}
}
