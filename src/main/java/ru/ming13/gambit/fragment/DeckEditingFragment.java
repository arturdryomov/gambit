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

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckNotSavedEvent;
import ru.ming13.gambit.bus.OperationSavedEvent;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.task.DeckEditingTask;
import ru.ming13.gambit.util.Fragments;

public class DeckEditingFragment extends Fragment
{
	@InjectView(R.id.edit_deck_title)
	TextView deckTitle;

	@InjectExtra(Fragments.Arguments.DECK)
	Deck deck;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_deck_operation, fragmentContainer, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpInjections();

		setUpDeck();
	}

	private void setUpInjections() {
		ButterKnife.inject(this, getView());

		Dart.inject(this);
	}

	private void setUpDeck() {
		deckTitle.append(deck.getTitle());
	}

	@Subscribe
	public void onOperationSaved(OperationSavedEvent event) {
		saveDeck();
	}

	private void saveDeck() {
		Deck deck = assembleDeck();

		if (isDeckCorrect(deck)) {
			saveDeck(deck);
		} else {
			showErrorMessage(deck);
		}
	}

	private Deck assembleDeck() {
		return new Deck(deck.getId(), getDeckTitle());
	}

	private String getDeckTitle() {
		return deckTitle.getText().toString().trim();
	}

	private boolean isDeckCorrect(Deck deck) {
		return !deck.getTitle().isEmpty();
	}

	private void saveDeck(Deck deck) {
		DeckEditingTask.execute(getActivity().getContentResolver(), deck);
	}

	private void showErrorMessage(Deck deck) {
		if (deck.getTitle().isEmpty()) {
			deckTitle.setError(getString(R.string.error_empty_field));
		} else {
			deckTitle.setError(getString(R.string.error_deck_already_exists));
		}
	}

	@Subscribe
	public void onDeckNotSaved(DeckNotSavedEvent event) {
		showErrorMessage(assembleDeck());
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
