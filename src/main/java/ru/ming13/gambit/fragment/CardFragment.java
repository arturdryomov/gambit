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

import ru.ming13.gambit.R;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.util.Fragments;

public class CardFragment extends Fragment implements View.OnClickListener
{
	private static enum CardSide
	{
		FRONT, BACK
	}

	private CardSide currentCardSide = CardSide.FRONT;

	public static CardFragment newInstance(Card card) {
		CardFragment cardFragment = new CardFragment();

		cardFragment.setArguments(buildArguments(card));

		return cardFragment;
	}

	private static Bundle buildArguments(Card card) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.CARD, card);

		return arguments;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_card, fragmentContainer, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpCard();
	}

	private void setUpCard() {
		setUpCardListener();
		setUpCardText();
	}

	private void setUpCardListener() {
		getCardView().setOnClickListener(this);
	}

	private TextView getCardView() {
		return (TextView) getView().findViewById(R.id.text);
	}

	@Override
	public void onClick(View view) {
		flipCard();

		setUpCardText();
	}

	private void flipCard() {
		currentCardSide = getFlippedCardSide();
	}

	private CardSide getFlippedCardSide() {
		switch (currentCardSide) {
			case FRONT:
				return CardSide.BACK;

			default:
				return CardSide.FRONT;
		}
	}

	private void setUpCardText() {
		setUpCardText(getCurrentCardText());
	}

	private String getCurrentCardText() {
		switch (currentCardSide) {
			case FRONT:
				return getCard().getFrontSideText();

			default:
				return getCard().getBackSideText();
		}
	}

	private Card getCard() {
		return getArguments().getParcelable(Fragments.Arguments.CARD);
	}

	private void setUpCardText(String text) {
		getCardView().setText(text);
	}
}
