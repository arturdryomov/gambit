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


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.gambit.R;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.CardEditedEvent;
import ru.ming13.gambit.ui.bus.CardEditingCancelledEvent;
import ru.ming13.gambit.ui.bus.CardQueriedEvent;
import ru.ming13.gambit.ui.task.CardEditingTask;
import ru.ming13.gambit.ui.task.CardQueryingTask;


public class CardEditingFragment extends FormFragment
{
	private Uri cardUri;

	private String cardOriginalFrontSideText;
	private String cardOriginalBackSideText;
	private String cardFrontSideText;
	private String cardBackSideText;

	public static CardEditingFragment newInstance(Uri cardUri) {
		CardEditingFragment cardEditingFragment = new CardEditingFragment();

		cardEditingFragment.setArguments(buildArguments(cardUri));

		return cardEditingFragment;
	}

	private static Bundle buildArguments(Uri cardUri) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(FragmentArguments.CARD_URI, cardUri);

		return bundle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cardUri = getArguments().getParcelable(FragmentArguments.CARD_URI);
	}

	@Override
	public void onStart() {
		super.onStart();

		queryCardOriginalSidesText();
	}

	private void queryCardOriginalSidesText() {
		CardQueryingTask.execute(getActivity().getContentResolver(), cardUri);
	}

	@Subscribe
	public void onCardQueried(CardQueriedEvent cardQueriedEvent) {
		cardOriginalFrontSideText = cardQueriedEvent.getCardFrontSideText();
		cardOriginalBackSideText = cardQueriedEvent.getCardBackSideText();

		setUpCardOriginalSidesText();
	}

	private void setUpCardOriginalSidesText() {
		if (StringUtils.isBlank(getTextFromEdit(R.id.edit_front_side_text))) {
			setTextToEdit(R.id.edit_front_side_text, cardOriginalFrontSideText);
		}

		if (StringUtils.isBlank(getTextFromEdit(R.id.edit_back_side_text))) {
			setTextToEdit(R.id.edit_back_side_text, cardOriginalBackSideText);
		}
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
		if (isCardSidesTextNotChanged()) {
			BusProvider.getInstance().post(new CardEditedEvent());
			return;
		}

		CardEditingTask.execute(getActivity().getContentResolver(), cardUri, cardFrontSideText,
			cardBackSideText);
	}

	private boolean isCardSidesTextNotChanged() {
		return cardFrontSideText.equals(cardOriginalFrontSideText) && cardBackSideText.equals(
			cardOriginalBackSideText);
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
		BusProvider.getInstance().post(new CardEditingCancelledEvent());
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
