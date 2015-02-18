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
import android.widget.EditText;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.OperationSavedEvent;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.task.CardCreationTask;
import ru.ming13.gambit.util.Fragments;

public class CardCreationFragment extends Fragment
{
	@InjectView(R.id.edit_front_side_text)
	EditText frontSide;

	@InjectView(R.id.edit_back_side_text)
	EditText backSide;

	@InjectExtra(Fragments.Arguments.DECK)
	Deck deck;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_card_operation, fragmentContainer, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpInjections();
	}

	private void setUpInjections() {
		ButterKnife.inject(this, getView());

		Dart.inject(this);
	}

	@Subscribe
	public void onOperationSaved(OperationSavedEvent event) {
		saveCard();
	}

	private void saveCard() {
		Card card = assembleCard();

		if (isCardCorrect(card)) {
			saveCard(card);
		} else {
			showErrorMessage(card);
		}
	}

	private Card assembleCard() {
		return new Card(getCardFrontSideText(), getCardBackSideText());
	}

	private String getCardFrontSideText() {
		return frontSide.getText().toString().trim();
	}

	private String getCardBackSideText() {
		return backSide.getText().toString().trim();
	}

	private boolean isCardCorrect(Card card) {
		return !card.getFrontSideText().isEmpty() && !card.getBackSideText().isEmpty();
	}

	private void saveCard(Card card) {
		CardCreationTask.execute(getActivity().getContentResolver(), deck, card);
	}

	private void showErrorMessage(Card card) {
		if (card.getFrontSideText().isEmpty()) {
			frontSide.setError(getString(R.string.error_empty_field));
		}

		if (card.getBackSideText().isEmpty()) {
			backSide.setError(getString(R.string.error_empty_field));
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		tearDownInjections();
	}

	private void tearDownInjections() {
		ButterKnife.reset(this);
	}
}
