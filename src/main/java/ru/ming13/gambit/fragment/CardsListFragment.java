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
import android.util.SparseBooleanArray;
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
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.CardsDeletionTask;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.Loaders;

public class CardsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ListView.MultiChoiceModeListener
{
	public static CardsListFragment newInstance(Deck deck) {
		CardsListFragment fragment = new CardsListFragment();

		fragment.setArguments(buildArguments(deck));

		return fragment;
	}

	private static Bundle buildArguments(Deck deck) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.DECK, deck);

		return arguments;
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
		String sort = GambitContract.Cards.FRONT_SIDE_TEXT;

		return new CursorLoader(getActivity(), getCardsUri(), null, null, null, sort);
	}

	private Uri getCardsUri() {
		return GambitContract.Cards.getCardsUri(getDeck().getId());
	}

	private Deck getDeck() {
		return getArguments().getParcelable(Fragments.Arguments.DECK);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cardsLoader, Cursor cardsCursor) {
		setUpCardsAnimations();

		getCardsAdapter().swapCursor(cardsCursor);

		setUpCardsMessage();
	}

	private void setUpCardsAnimations() {
		if (!getCardsAdapter().isEmpty()) {
			TransitionManager.beginDelayedTransition(getListView());
		}
	}

	private CardsListAdapter getCardsAdapter() {
		return (CardsListAdapter) getListAdapter();
	}

	private void setUpCardsMessage() {
		if (getCardsAdapter().isEmpty()) {
			showCardsMessage();
		} else {
			hideCardsMessage();
		}
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
				startCardsDeletion(getCheckedCards());
				break;

			default:
				return false;
		}

		actionMode.finish();

		return true;
	}

	private void startCardsDeletion(List<Card> cards) {
		CardsDeletionTask.execute(getActivity().getContentResolver(), getDeck(), cards);
	}

	private List<Card> getCheckedCards() {
		List<Card> cards = new ArrayList<Card>();

		SparseBooleanArray checkedCardsPositions = getCheckedCardsPositions();

		for (int cardPosition = 0; cardPosition < checkedCardsPositions.size(); cardPosition++) {
			if (checkedCardsPositions.valueAt(cardPosition)) {
				cards.add(getCard(checkedCardsPositions.keyAt(cardPosition)));
			}
		}

		return cards;
	}

	private SparseBooleanArray getCheckedCardsPositions() {
		return getListView().getCheckedItemPositions();
	}

	private Card getCard(int cardPosition) {
		Cursor cardsCursor = getCardsCursor(cardPosition);

		long cardId = cardsCursor.getLong(
			cardsCursor.getColumnIndex(GambitContract.Cards._ID));
		String cardFrontSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
		String cardBackSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));

		return new Card(cardId, cardFrontSideText, cardBackSideText);
	}

	private Cursor getCardsCursor(int cardPosition) {
		return (Cursor) getCardsAdapter().getItem(cardPosition);
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		startCardEditingActivity(getCard(position));
	}

	private void startCardEditingActivity(Card card) {
		Intent intent = Intents.Builder.with(getActivity()).buildCardEditingIntent(getDeck(), card);
		startActivity(intent);
	}
}
