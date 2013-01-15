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


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.provider.Uris;
import ru.ming13.gambit.local.sqlite.DbFieldNames;
import ru.ming13.gambit.ui.loader.Loaders;


public class DecksFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	private CursorAdapter decksAdapter;

	public static DecksFragment newInstance() {
		return new DecksFragment();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpDecksList();
	}

	private void setUpDecksList() {
		setUpDecksLoadingText();
		setUpDecksAdapter();

		loadDecks();
	}

	private void setUpDecksLoadingText() {
		TextView emptyDecksListTextView = (TextView) getListView().getEmptyView();
		emptyDecksListTextView.setText(R.string.loading_decks);
	}

	private void setUpDecksAdapter() {
		decksAdapter = buildDecksAdapter();
		setListAdapter(decksAdapter);
	}

	private CursorAdapter buildDecksAdapter() {
		String[] departureColumns = {DbFieldNames.DECK_TITLE};
		int[] destinationFields = {R.id.text};

		return new SimpleCursorAdapter(getActivity(), R.layout.list_item_one_line, null,
			departureColumns, destinationFields, 0);
	}

	private void loadDecks() {
		getLoaderManager().initLoader(Loaders.DECKS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		String[] projection = {DbFieldNames.ID, DbFieldNames.DECK_TITLE};
		String sort = DbFieldNames.DECK_TITLE;

		return new CursorLoader(getActivity(), Uris.Content.DECKS, projection, null, null, sort);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> decksLoader, Cursor decksCursor) {
		decksAdapter.changeCursor(decksCursor);

		if (getListAdapter().isEmpty()) {
			setUpNoDecksText();
		}
	}

	private void setUpNoDecksText() {
		TextView emptyDecksListTextView = (TextView) getListView().getEmptyView();
		emptyDecksListTextView.setText(R.string.empty_decks);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> decksLoader) {
		decksAdapter.changeCursor(null);
	}
}
