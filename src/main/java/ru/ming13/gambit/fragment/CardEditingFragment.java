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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardAssembledEvent;
import ru.ming13.gambit.bus.OperationSavedEvent;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.util.Fragments;

public class CardEditingFragment extends Fragment
{
	public static CardEditingFragment newInstance(Card card) {
		CardEditingFragment fragment = new CardEditingFragment();

		fragment.setArguments(buildArguments(card));

		return fragment;
	}

	private static Bundle buildArguments(Card card) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.CARD, card);

		return arguments;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_card_operation, fragmentContainer, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpCard();
	}

	private void setUpCard() {
		getCardFrontSideTextView().append(getCard().getFrontSideText());
		getCardBackSideTextView().append(getCard().getBackSideText());
	}

	private TextView getCardFrontSideTextView() {
		return (TextView) getView().findViewById(R.id.edit_front_side_text);
	}

	private Card getCard() {
		return getArguments().getParcelable(Fragments.Arguments.CARD);
	}

	private TextView getCardBackSideTextView() {
		return (TextView) getView().findViewById(R.id.edit_back_side_text);
	}

	@Subscribe
	public void onOperationSaved(OperationSavedEvent event) {
		saveCard();
	}

	private void saveCard() {
		if (isCardCorrect()) {
			assembleCard();
		} else {
			showErrorMessage();
		}
	}

	private boolean isCardCorrect() {
		return !getCardFrontSideText().isEmpty() && !getCardBackSideText().isEmpty();
	}

	private String getCardFrontSideText() {
		return getCardFrontSideTextView().getText().toString().trim();
	}

	private String getCardBackSideText() {
		return getCardBackSideTextView().getText().toString().trim();
	}

	private void assembleCard() {
		Card card = new Card(getCard().getId(), getCardFrontSideText(), getCardBackSideText());

		BusProvider.getBus().post(new CardAssembledEvent(card));
	}

	private void showErrorMessage() {
		if (getCardFrontSideText().isEmpty()) {
			getCardFrontSideTextView().setError(getString(R.string.error_empty_field));
		}

		if (getCardBackSideText().isEmpty()) {
			getCardBackSideTextView().setError(getString(R.string.error_empty_field));
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
