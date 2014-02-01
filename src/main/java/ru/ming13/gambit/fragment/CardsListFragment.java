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
import ru.ming13.gambit.adapter.CardsListAdapter;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.CardsDeletionTask;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.Loaders;


public class CardsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ListView.MultiChoiceModeListener
{
	public static CardsListFragment newInstance(Uri cardsUri) {
		CardsListFragment cardsFragment = new CardsListFragment();

		cardsFragment.setArguments(buildArguments(cardsUri));

		return cardsFragment;
	}

	private static Bundle buildArguments(Uri cardsUri) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(Fragments.Arguments.URI, cardsUri);

		return bundle;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpCards();
	}

	private void setUpCards() {
		setUpCardsAdapter();
		setUpCardsContent();
		setUpCardsActions();
	}

	private void setUpCardsAdapter() {
		setListAdapter(new CardsListAdapter(getActivity()));
	}

	private void setUpCardsContent() {
		getLoaderManager().initLoader(Loaders.CARDS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		Uri uri = getCardsUri();

		String[] projection = {GambitContract.Cards._ID, GambitContract.Cards.BACK_SIDE_TEXT, GambitContract.Cards.FRONT_SIDE_TEXT};
		String sort = GambitContract.Cards.FRONT_SIDE_TEXT;

		return new CursorLoader(getActivity(), uri, projection, null, null, sort);
	}

	private Uri getCardsUri() {
		return getArguments().getParcelable(Fragments.Arguments.URI);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cardsLoader, Cursor cardsCursor) {
		getCardsAdapter().swapCursor(cardsCursor);

		if (getCardsAdapter().isEmpty()) {
			showCardsMessage();
		} else {
			hideCardsMessage();
		}
	}

	private CardsListAdapter getCardsAdapter() {
		return (CardsListAdapter) getListAdapter();
	}

	private void showCardsMessage() {
		TextView messageTitleTextView = (TextView) getView().findViewById(R.id.text_message_title);
		TextView messageSummaryTextView = (TextView) getView().findViewById(R.id.text_message_summary);

		messageTitleTextView.setText(R.string.empty_cards_title);
		messageSummaryTextView.setText(R.string.empty_cards_subtitle);

		getView().findViewById(R.id.layout_message).setVisibility(View.VISIBLE);
	}

	private void hideCardsMessage() {
		getView().findViewById(R.id.layout_message).setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cardsLoader) {
		getCardsAdapter().swapCursor(null);
	}

	private void setUpCardsActions() {
		getListView().setMultiChoiceModeListener(this);
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		actionMode.getMenuInflater().inflate(R.menu.context_cards_list, menu);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_delete:
				startCardsDeletion();
				return true;

			default:
				return false;
		}
	}

	private void startCardsDeletion() {
		CardsDeletionTask.execute(getActivity().getContentResolver(), getCheckedCardUris());
	}

	private List<Uri> getCheckedCardUris() {
		List<Uri> checkedCardUris = new ArrayList<Uri>();

		for (long checkedCardId : getListView().getCheckedItemIds()) {
			checkedCardUris.add(GambitContract.Cards.getCardUri(getCardsUri(), checkedCardId));
		}

		return checkedCardUris;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		startCardEditingActivity(GambitContract.Cards.getCardUri(getCardsUri(), id));
	}

	private void startCardEditingActivity(Uri cardUri) {
		Intent intent = Intents.Builder.with(getActivity()).buildCardEditingIntent(cardUri);
		startActivity(intent);
	}
}
