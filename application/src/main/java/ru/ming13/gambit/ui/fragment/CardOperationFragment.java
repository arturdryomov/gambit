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
import ru.ming13.gambit.local.model.Card;
import ru.ming13.gambit.local.model.Deck;
import ru.ming13.gambit.ui.loader.CardOperationLoader;
import ru.ming13.gambit.ui.loader.Loaders;
import ru.ming13.gambit.ui.loader.result.LoaderResult;


public class CardOperationFragment extends FormFragment implements LoaderManager.LoaderCallbacks<LoaderResult<Card>>
{
	private static enum Operation
	{
		CREATE, MODIFY
	}

	private Operation operation;

	private Deck deck;
	private Card card;
	private String frontSideText;
	private String backSideText;

	public static CardOperationFragment newCreationInstance(Deck deck) {
		CardOperationFragment cardOperationFragment = new CardOperationFragment();

		cardOperationFragment.setArguments(buildArguments(Operation.CREATE, deck));

		return cardOperationFragment;
	}

	private static Bundle buildArguments(Operation operation, Deck deck) {
		Bundle arguments = new Bundle();

		arguments.putSerializable(FragmentArguments.OPERATION, operation);
		arguments.putParcelable(FragmentArguments.DECK, deck);

		return arguments;
	}

	public static CardOperationFragment newModificationInstance(Card card) {
		CardOperationFragment cardOperationFragment = new CardOperationFragment();

		cardOperationFragment.setArguments(buildArguments(Operation.MODIFY, card));

		return cardOperationFragment;
	}

	private static Bundle buildArguments(Operation operation, Card card) {
		Bundle arguments = new Bundle();

		arguments.putSerializable(FragmentArguments.OPERATION, operation);
		arguments.putParcelable(FragmentArguments.CARD, card);

		return arguments;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		operation = (Operation) getArguments().getSerializable(FragmentArguments.OPERATION);
		deck = getArguments().getParcelable(FragmentArguments.DECK);
		card = getArguments().getParcelable(FragmentArguments.CARD);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpCardSidesText();
	}

	private void setUpCardSidesText() {
		if (operation == Operation.MODIFY) {
			setTextToEdit(R.id.edit_front_side_text, card.getFrontSideText());
			setTextToEdit(R.id.edit_back_side_text, card.getBackSideText());
		}
	}

	@Override
	protected View inflateFragment(LayoutInflater layoutInflater, ViewGroup fragmentContainer) {
		return layoutInflater.inflate(R.layout.fragment_card_operation, fragmentContainer, false);
	}

	@Override
	protected void readUserDataFromFields() {
		frontSideText = getTextFromEdit(R.id.edit_front_side_text);
		backSideText = getTextFromEdit(R.id.edit_back_side_text);
	}

	@Override
	protected boolean isUserDataCorrect() {
		return isFrontSideTextCorrect() && isBackSideTextCorrect();
	}

	private boolean isFrontSideTextCorrect() {
		return StringUtils.isNotBlank(frontSideText);
	}

	private boolean isBackSideTextCorrect() {
		return StringUtils.isNotBlank(backSideText);
	}

	@Override
	protected void setUpErrorMessages() {
		if (!isFrontSideTextCorrect()) {
			setErrorToEdit(R.id.edit_front_side_text, R.string.error_empty_field);
		}

		if (!isBackSideTextCorrect()) {
			setErrorToEdit(R.id.edit_back_side_text, R.string.error_empty_field);
		}
	}

	@Override
	protected <Data> void performAcceptAction(Data data) {
		getLoaderManager().initLoader(Loaders.CARD_OPERATION, null, this);
	}

	@Override
	public Loader<LoaderResult<Card>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (operation) {
			case CREATE:
				return CardOperationLoader.newCreationInstance(getActivity(), deck, frontSideText,
					backSideText);

			case MODIFY:
				return CardOperationLoader.newModificationInstance(getActivity(), card, frontSideText,
					backSideText);

			default:
				throw new FragmentException();
		}
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult<Card>> cardOperationLoader, LoaderResult<Card> cardOperationLoaderResult) {
		super.performAcceptAction(cardOperationLoaderResult.getData());
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult<Card>> cardOperationLoader) {
	}
}
