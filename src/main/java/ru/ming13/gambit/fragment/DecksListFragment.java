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
import android.support.transition.TransitionManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ming13.gambit.R;
import ru.ming13.gambit.adapter.DecksListAdapter;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.DecksDeletionTask;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.Loaders;


public class DecksListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ListView.MultiChoiceModeListener
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
		setUpDecksAdapter();
		setUpDecksContent();
		setUpDecksActions();
	}

	private void setUpDecksAdapter() {
		setListAdapter(new DecksListAdapter(getActivity()));
	}

	private void setUpDecksContent() {
		getLoaderManager().initLoader(Loaders.DECKS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		Uri uri = getDecksUri();

		String[] projection = {GambitContract.Decks._ID, GambitContract.Decks.TITLE};
		String sort = GambitContract.Decks.TITLE;

		return new CursorLoader(getActivity(), uri, projection, null, null, sort);
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
		getListView().setMultiChoiceModeListener(this);
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
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
		actionMode.getMenuInflater().inflate(R.menu.context_decks_list, menu);

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
				startDeckEditingActivity();
				break;

			case R.id.menu_edit_cards:
				startCardsListActivity();
				break;

			case R.id.menu_delete:
				startDecksDeletion();
				break;

			default:
				return false;
		}

		actionMode.finish();

		return true;
	}

	private void startDeckEditingActivity() {
		Intent intent = Intents.Builder.with(getActivity()).buildDeckEditingIntent(getCheckedDeckUri());
		startActivity(intent);
	}

	private Uri getCheckedDeckUri() {
		return getCheckedDeckUris().get(0);
	}

	private List<Uri> getCheckedDeckUris() {
		List<Uri> checkedDeckUris = new ArrayList<Uri>();

		for (long checkedDeckId : getListView().getCheckedItemIds()) {
			checkedDeckUris.add(GambitContract.Decks.getDeckUri(checkedDeckId));
		}

		return checkedDeckUris;
	}

	private void startCardsListActivity() {
		Intent intent = Intents.Builder.with(getActivity()).buildCardsListIntent(getCheckedDeckUri());
		startActivity(intent);
	}

	private void startDecksDeletion() {
		DecksDeletionTask.execute(getActivity().getContentResolver(), getCheckedDeckUris());
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		startCardsPagerActivity(GambitContract.Decks.getDeckUri(id));
	}

	private void startCardsPagerActivity(Uri deckUri) {
		Intent intent = Intents.Builder.with(getActivity()).buildCardsPagerIntent(deckUri);
		startActivity(intent);
	}
}
