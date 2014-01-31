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
	public void onStart() {
		super.onStart();

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
	}

	private void flipCard() {
		switch (currentCardSide) {
			case FRONT:
				currentCardSide = CardSide.BACK;
				break;

			case BACK:
				currentCardSide = CardSide.FRONT;
				break;

			default:
				currentCardSide = CardSide.FRONT;
				break;
		}

		setUpCardText();
	}

	private void setUpCardText() {
		switch (currentCardSide) {
			case FRONT:
				setUpCardText(getCard().getFrontSideText());
				break;

			case BACK:
				setUpCardText(getCard().getBackSideText());
				break;

			default:
				setUpCardText(getCard().getFrontSideText());
				break;
		}
	}

	private void setUpCardText(String text) {
		getCardView().setText(text);
	}

	private Card getCard() {
		return getArguments().getParcelable(Fragments.Arguments.CARD);
	}
}
