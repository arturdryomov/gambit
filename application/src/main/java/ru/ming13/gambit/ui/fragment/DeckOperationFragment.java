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


import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.model.Deck;
import ru.ming13.gambit.ui.loader.DeckOperationLoader;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.loader.result.LoaderResult;


public class DeckOperationFragment extends FormFragment implements LoaderManager.LoaderCallbacks<LoaderResult<Deck>>
{
	private static enum Operation
	{
		CREATE, RENAME
	}

	private Operation operation;

	private Deck deck;
	private String deckTitle;

	public static DeckOperationFragment newCreationInstance() {
		DeckOperationFragment deckOperationFragment = new DeckOperationFragment();

		deckOperationFragment.setArguments(buildArguments(Operation.CREATE));

		return deckOperationFragment;
	}

	private static Bundle buildArguments(Operation operation) {
		Bundle bundle = new Bundle();

		bundle.putSerializable(FragmentArguments.OPERATION, operation);

		return bundle;
	}

	public static DeckOperationFragment newRenamingInstance(Deck deck) {
		DeckOperationFragment deckOperationFragment = new DeckOperationFragment();

		deckOperationFragment.setArguments(buildArguments(Operation.RENAME, deck));

		return deckOperationFragment;
	}

	private static Bundle buildArguments(Operation operation, Deck deck) {
		Bundle bundle = new Bundle();

		bundle.putSerializable(FragmentArguments.OPERATION, operation);
		bundle.putParcelable(FragmentArguments.DECK, deck);

		return bundle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		operation = (Operation) getArguments().getSerializable(FragmentArguments.OPERATION);
		deck = getArguments().getParcelable(FragmentArguments.DECK);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpDeckTitle();
	}

	private void setUpDeckTitle() {
		if (operation == Operation.RENAME) {
			setTextToEdit(R.id.edit_deck_title, deck.getTitle());
		}
	}

	@Override
	protected View inflateFragment(LayoutInflater layoutInflater, ViewGroup fragmentContainer) {
		return layoutInflater.inflate(R.layout.fragment_deck_operation, fragmentContainer, false);
	}

	@Override
	protected void readUserDataFromFields() {
		deckTitle = getTextFromEdit(R.id.edit_deck_title);
	}

	@Override
	protected boolean isUserDataCorrect() {
		return isDeckNameCorrect();
	}

	private boolean isDeckNameCorrect() {
		return StringUtils.isNotBlank(deckTitle);
	}

	@Override
	protected void setUpErrorMessages() {
		if (!isDeckNameCorrect()) {
			setErrorToEdit(R.id.edit_deck_title, R.string.error_empty_field);
		}
	}

	@Override
	protected <Data> void performAcceptAction(Data data) {
		getLoaderManager().initLoader(Loaders.DECK_OPERATION, null, this);
	}

	@Override
	public Loader<LoaderResult<Deck>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (operation) {
			case CREATE:
				return DeckOperationLoader.newCreationLoader(getActivity(), deckTitle);

			case RENAME:
				return DeckOperationLoader.newRenamingLoader(getActivity(), deck, deckTitle);

			default:
				throw new FragmentException();
		}
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult<Deck>> deckOperationLoader, LoaderResult<Deck> deckOperationLoaderResult) {
		switch (deckOperationLoaderResult.getStatus()) {
			case ERROR:
				setErrorToEdit(R.id.edit_deck_title, deckOperationLoaderResult.getErrorMessage());
				break;

			case SUCCESS:
				super.performAcceptAction(deckOperationLoaderResult.getData());
				break;

			default:
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult<Deck>> deckOperationLoader) {
	}
}
