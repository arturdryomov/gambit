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


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import ru.ming13.gambit.R;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.ui.intent.IntentFactory;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.task.CardDeletionTask;
import ru.ming13.gambit.ui.util.ActionModeProvider;


public class CardsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionModeProvider.ContextMenuHandler
{
	private Uri cardsUri;

	private CursorAdapter cardsAdapter;

	public static CardsFragment newInstance(Uri deckUri) {
		CardsFragment cardsFragment = new CardsFragment();

		cardsFragment.setArguments(buildArguments(deckUri));

		return cardsFragment;
	}

	private static Bundle buildArguments(Uri deckUri) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(FragmentArguments.DECK_URI, deckUri);

		return bundle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpCardsUri();

		setUpHomeButton();

		setHasOptionsMenu(true);
	}

	private void setUpCardsUri() {
		Uri deckUri = getArguments().getParcelable(FragmentArguments.DECK_URI);

		cardsUri = GambitContract.Cards.buildCardsUri(deckUri);
	}

	private void setUpHomeButton() {
		getActivity().getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpCardsList();
	}

	private void setUpCardsList() {
		setUpCardsAdapter();
		loadCards();
	}

	private void setUpCardsAdapter() {
		cardsAdapter = buildCardsAdapter();
		setListAdapter(cardsAdapter);
	}

	private CursorAdapter buildCardsAdapter() {
		String[] departureColumns = {GambitContract.Cards.FRONT_SIDE_TEXT};
		int[] destinationFields = {R.id.text};

		SimpleCursorAdapter cardsAdapter = new SimpleCursorAdapter(getActivity(),
			R.layout.list_item_one_line, null, departureColumns, destinationFields, 0);

		cardsAdapter.setViewBinder(buildCardsListItemViewBinder());

		return cardsAdapter;
	}

	private SimpleCursorAdapter.ViewBinder buildCardsListItemViewBinder() {
		String cardsListItemTextMask = getString(R.string.mask_card_list_item);

		return new CardsListItemViewBinder(cardsListItemTextMask);
	}

	private static class CardsListItemViewBinder implements SimpleCursorAdapter.ViewBinder
	{
		private final String cardsListItemTextMask;

		public CardsListItemViewBinder(String cardsListItemTextMask) {
			this.cardsListItemTextMask = cardsListItemTextMask;
		}

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			TextView cardsListItemTextView = (TextView) view;
			cardsListItemTextView.setText(buildCardsListItemText(cursor));

			return true;
		}

		private String buildCardsListItemText(Cursor cursor) {
			String cardFrontSideText = cursor.getString(
				cursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
			String cardBackSideText = cursor.getString(
				cursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));

			return String.format(cardsListItemTextMask, cardFrontSideText, cardBackSideText);
		}
	}

	private void loadCards() {
		getLoaderManager().initLoader(Loaders.CARDS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		String[] projection = {GambitContract.Cards._ID, GambitContract.Cards.FRONT_SIDE_TEXT,
			GambitContract.Cards.BACK_SIDE_TEXT};
		String sort = GambitContract.Cards.FRONT_SIDE_TEXT;

		return new CursorLoader(getActivity(), cardsUri, projection, null, null, sort);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cardsLoader, Cursor cursor) {
		cardsAdapter.swapCursor(cursor);

		if (getListAdapter().isEmpty()) {
			showNoCardsText();
		}
		else {
			hideNoCardsText();
		}
	}

	private void showNoCardsText() {
		TextView emptyCardsTitleTextView = (TextView) getView().findViewById(R.id.empty_title);
		TextView emptyCardsSubtitleTextView = (TextView) getView().findViewById(R.id.empty_subtitle);

		setNoCardsTextVisibility(View.VISIBLE);

		emptyCardsTitleTextView.setText(R.string.empty_cards_title);
		emptyCardsSubtitleTextView.setText(R.string.empty_cards_subtitle);
	}

	private void setNoCardsTextVisibility(int visibility) {
		TextView emptyCardsTitleTextView = (TextView) getView().findViewById(R.id.empty_title);
		TextView emptyCardsSubtitleTextView = (TextView) getView().findViewById(R.id.empty_subtitle);

		emptyCardsTitleTextView.setVisibility(visibility);
		emptyCardsSubtitleTextView.setVisibility(visibility);
	}

	private void hideNoCardsText() {
		setNoCardsTextVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cardsLoader) {
		cardsAdapter.swapCursor(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_action_bar_cards, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				navigateUp();
				return true;

			case R.id.menu_new_card:
				callCardCreation();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void navigateUp() {
		Intent intent = IntentFactory.createDecksIntent(getActivity());
		NavUtils.navigateUpTo(getActivity(), intent);
	}

	private void callCardCreation() {
		Intent intent = IntentFactory.createCardCreationIntent(getActivity(), cardsUri);
		startActivity(intent);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		Uri cardUri = GambitContract.Cards.buildCardUri(cardsUri, id);

		callCardModification(cardUri);
	}

	private void callCardModification(Uri cardUri) {
		Intent intent = IntentFactory.createCardModificationIntent(getActivity(), cardUri);
		startActivity(intent);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpContextMenu();
	}

	private void setUpContextMenu() {
		if (ActionModeProvider.isActionModeAvailable()) {
			ActionModeProvider.setUpActionMode(getListView(), this, R.menu.menu_context_cards);
		}
		else {
			registerForContextMenu(getListView());
		}
	}

	@Override
	public boolean handleContextMenu(android.view.MenuItem menuItem, long listItemId) {
		Uri cardUri = GambitContract.Cards.buildCardUri(cardsUri, listItemId);

		switch (menuItem.getItemId()) {
			case R.id.menu_edit:
				callCardModification(cardUri);
				return true;

			case R.id.menu_delete:
				callCardDeletion(cardUri);
				return true;

			default:
				return false;
		}
	}

	private void callCardDeletion(Uri cardUri) {
		CardDeletionTask.execute(getActivity().getContentResolver(), cardUri);
	}

	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
		super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

		getActivity().getMenuInflater().inflate(R.menu.menu_context_cards, contextMenu);
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
