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

import android.app.Fragment;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.ming13.gambit.R;
import ru.ming13.gambit.adapter.CardsListAdapter;
import ru.ming13.gambit.cursor.CardsCursor;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.CardsDeletionTask;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.Loaders;

public class CardsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ListView.MultiChoiceModeListener, AdapterView.OnItemClickListener
{
	@InjectView(android.R.id.list)
	AbsListView cardsList;

	@InjectView(R.id.button_action)
	FloatingActionButton actionButton;

	@InjectView(R.id.layout_message)
	ViewGroup messageLayout;

	@InjectView(R.id.text_message_title)
	TextView messageTitle;

	@InjectView(R.id.text_message_summary)
	TextView messageSummary;

	@InjectExtra(Fragments.Arguments.DECK)
	Deck deck;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_cards_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpInjections();

		setUpCards();
	}

	private void setUpInjections() {
		ButterKnife.inject(this, getView());

		Dart.inject(this);
	}

	private void setUpCards() {
		setUpCardsAdapter();
		setUpCardsContent();
		setUpCardsActions();
		setUpCardsListener();
	}

	private void setUpCardsAdapter() {
		cardsList.setAdapter(new CardsListAdapter(getActivity()));
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
		return GambitContract.Cards.getCardsUri(deck.getId());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cardsLoader, Cursor cardsCursor) {
		setUpCardsAnimations();

		getCardsAdapter().swapCursor(new CardsCursor(cardsCursor));

		setUpCardsMessage();
	}

	private void setUpCardsAnimations() {
		if (!getCardsAdapter().isEmpty()) {
			TransitionManager.beginDelayedTransition(cardsList);
		}
	}

	private CardsListAdapter getCardsAdapter() {
		return (CardsListAdapter) cardsList.getAdapter();
	}

	private void setUpCardsMessage() {
		if (getCardsAdapter().isEmpty()) {
			showCardsMessage();
		} else {
			hideCardsMessage();
		}
	}

	private void showCardsMessage() {
		messageTitle.setText(R.string.empty_cards_title);
		messageSummary.setText(R.string.empty_cards_subtitle);

		messageLayout.setVisibility(View.VISIBLE);
	}

	private void hideCardsMessage() {
		messageLayout.setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cardsLoader) {
	}

	private void setUpCardsActions() {
		actionButton.attachToListView(cardsList);

		cardsList.setMultiChoiceModeListener(this);
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		actionMode.getMenuInflater().inflate(R.menu.action_mode_cards_list, menu);

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
		CardsDeletionTask.execute(getActivity().getContentResolver(), deck, cards);
	}

	private List<Card> getCheckedCards() {
		List<Card> cards = new ArrayList<>();

		SparseBooleanArray checkedCardsPositions = getCheckedCardsPositions();

		for (int cardPosition = 0; cardPosition < checkedCardsPositions.size(); cardPosition++) {
			if (checkedCardsPositions.valueAt(cardPosition)) {
				cards.add(getCard(checkedCardsPositions.keyAt(cardPosition)));
			}
		}

		return cards;
	}

	private SparseBooleanArray getCheckedCardsPositions() {
		return cardsList.getCheckedItemPositions();
	}

	private Card getCard(int cardPosition) {
		return getCardsAdapter().getItem(cardPosition);
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
	}

	private void setUpCardsListener() {
		cardsList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> cardsListView, View cardView, int cardPosition, long cardId) {
		startCardEditingActivity(getCard(cardPosition));
	}

	private void startCardEditingActivity(Card card) {
		Intent intent = Intents.Builder.with(getActivity()).buildCardEditingIntent(deck, card);
		startActivity(intent);
	}

	@OnClick(R.id.button_action)
	public void startCardCreation() {
		Intent intent = Intents.Builder.with(getActivity()).buildCardCreationIntent(deck);
		startActivity(intent);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		tearDownInjections();
	}

	private void tearDownInjections() {
		ButterKnife.reset(this);
	}
}
