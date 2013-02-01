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


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import ru.ming13.gambit.R;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.task.DeckDeletionTask;
import ru.ming13.gambit.ui.util.ActionModeProvider;


public class DecksFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionModeProvider.ContextMenuHandler
{
	private CursorAdapter decksAdapter;

	public static DecksFragment newInstance() {
		return new DecksFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpDecksList();
	}

	private void setUpDecksList() {
		setUpDecksAdapter();
		loadDecks();
	}

	private void setUpDecksAdapter() {
		decksAdapter = buildDecksAdapter();
		setListAdapter(decksAdapter);
	}

	private CursorAdapter buildDecksAdapter() {
		String[] departureColumns = {GambitContract.Decks.TITLE};
		int[] destinationFields = {R.id.text};

		return new SimpleCursorAdapter(getActivity(), R.layout.list_item_one_line, null,
			departureColumns, destinationFields, 0);
	}

	private void loadDecks() {
		getLoaderManager().initLoader(Loaders.DECKS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		String[] projection = {GambitContract.Decks._ID, GambitContract.Decks.TITLE};
		String sort = GambitContract.Decks.TITLE;

		return new CursorLoader(getActivity(), GambitContract.Decks.CONTENT_URI, projection, null, null,
			sort);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> decksLoader, Cursor decksCursor) {
		decksAdapter.swapCursor(decksCursor);

		if (getListAdapter().isEmpty()) {
			showNoDecksText();
		}
		else {
			hideNoDecksText();
		}
	}

	private void showNoDecksText() {
		TextView emptyDecksTitleTextView = (TextView) getView().findViewById(R.id.empty_title);
		TextView emptyDecksSubtitleTextView = (TextView) getView().findViewById(R.id.empty_subtitle);

		setNoDecksTextVisibility(View.VISIBLE);

		emptyDecksTitleTextView.setText(R.string.empty_decks_title);
		emptyDecksSubtitleTextView.setText(R.string.empty_decks_subtitle);
	}

	private void setNoDecksTextVisibility(int visibility) {
		TextView emptyDecksTitleTextView = (TextView) getView().findViewById(R.id.empty_title);
		TextView emptyDecksSubtitleTextView = (TextView) getView().findViewById(R.id.empty_subtitle);

		emptyDecksTitleTextView.setVisibility(visibility);
		emptyDecksSubtitleTextView.setVisibility(visibility);
	}

	private void hideNoDecksText() {
		setNoDecksTextVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> decksLoader) {
		decksAdapter.swapCursor(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_action_bar_decks, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_create_item:
				callDeckCreation();
				return true;

			case R.id.menu_licenses:
				callLicenses();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callDeckCreation() {
		Intent intent = IntentFactory.createDeckCreationIntent(getActivity());
		startActivity(intent);
	}

	private void callLicenses() {
		Intent intent = IntentFactory.createLicensesIntent(getActivity());
		startActivity(intent);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		Uri deckUri = GambitContract.Decks.buildDeckUri(id);

		callCardsPager(deckUri);
	}

	private void callCardsPager(Uri deckUri) {
		Intent intent = IntentFactory.createCardsPagerIntent(getActivity(), deckUri);
		startActivity(intent);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpContextMenu();
	}

	private void setUpContextMenu() {
		if (ActionModeProvider.isActionModeAvailable()) {
			ActionModeProvider.setUpActionMode(getListView(), this, R.menu.menu_context_decks);
		}
		else {
			registerForContextMenu(getListView());
		}
	}

	@Override
	public boolean handleContextMenu(android.view.MenuItem menuItem, long listItemId) {
		Uri deckUri = GambitContract.Decks.buildDeckUri(listItemId);

		switch (menuItem.getItemId()) {
			case R.id.menu_rename:
				callDeckRenaming(deckUri);
				return true;

			case R.id.menu_edit_cards:
				callCardsList(deckUri);
				return true;

			case R.id.menu_delete:
				callDeckDeletion(deckUri);
				return true;

			default:
				return false;
		}
	}

	private void callDeckRenaming(Uri deckUri) {
		Intent intent = IntentFactory.createDeckRenamingIntent(getActivity(), deckUri);
		startActivity(intent);
	}

	private void callCardsList(Uri deckUri) {
		Intent intent = IntentFactory.createCardsIntent(getActivity(), deckUri);
		startActivity(intent);
	}

	private void callDeckDeletion(Uri deckUri) {
		DeckDeletionTask.execute(getActivity().getContentResolver(), deckUri);
	}

	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
		super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

		getActivity().getMenuInflater().inflate(R.menu.menu_context_decks, contextMenu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem menuItem) {
		long cardListId = getListItemId(menuItem);

		return handleContextMenu(menuItem, cardListId);
	}

	private long getListItemId(android.view.MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo menuItemInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

		return menuItemInfo.id;
	}
}
