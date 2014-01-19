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


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardCreationCancelledEvent;
import ru.ming13.gambit.task.CardCreationTask;


public class CardCreationFragment extends FormFragment
{
	private Uri cardsUri;

	private String cardFrontSideText;
	private String cardBackSideText;

	public static CardCreationFragment newInstance(Uri cardsUri) {
		CardCreationFragment cardCreationFragment = new CardCreationFragment();

		cardCreationFragment.setArguments(buildArguments(cardsUri));

		return cardCreationFragment;
	}

	private static Bundle buildArguments(Uri cardsUri) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(FragmentArguments.CARDS_URI, cardsUri);

		return arguments;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cardsUri = getArguments().getParcelable(FragmentArguments.CARDS_URI);
	}

	@Override
	protected View inflateFragment(LayoutInflater layoutInflater, ViewGroup fragmentContainer) {
		return layoutInflater.inflate(R.layout.fragment_card_operation, fragmentContainer, false);
	}

	@Override
	protected void readUserDataFromFields() {
		cardFrontSideText = getTextFromEdit(R.id.edit_front_side_text);
		cardBackSideText = getTextFromEdit(R.id.edit_back_side_text);
	}

	@Override
	protected boolean isUserDataCorrect() {
		return StringUtils.isNotBlank(cardFrontSideText) && StringUtils.isNotBlank(cardBackSideText);
	}

	@Override
	protected void performAcceptAction() {
		CardCreationTask.execute(getActivity().getContentResolver(), cardsUri, cardFrontSideText,
			cardBackSideText);
	}

	@Override
	protected void setUpErrorMessages() {
		if (StringUtils.isBlank(cardFrontSideText)) {
			setErrorToEdit(R.id.edit_front_side_text, R.string.error_empty_field);
		}

		if (StringUtils.isBlank(cardBackSideText)) {
			setErrorToEdit(R.id.edit_back_side_text, R.string.error_empty_field);
		}
	}

	@Override
	protected void performCancelAction() {
		BusProvider.getInstance().post(new CardCreationCancelledEvent());
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getInstance().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		BusProvider.getInstance().unregister(this);
	}
}
