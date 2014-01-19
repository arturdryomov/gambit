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


public class CardFragment extends Fragment implements View.OnClickListener
{
	private static enum CardSide
	{
		FRONT, BACK
	}

	private String cardFrontSideText;
	private String cardBackSideText;
	private CardSide currentCardSide = CardSide.FRONT;

	public static CardFragment newInstance(String cardFrontSideText, String cardBackSideText) {
		CardFragment cardFragment = new CardFragment();

		cardFragment.setArguments(buildArguments(cardFrontSideText, cardBackSideText));

		return cardFragment;
	}

	private static Bundle buildArguments(String cardFrontSideText, String cardBackSideText) {
		Bundle arguments = new Bundle();

		arguments.putString(FragmentArguments.CARD_FRONT_SIDE_TEXT, cardFrontSideText);
		arguments.putString(FragmentArguments.CARD_BACK_SIDE_TEXT, cardBackSideText);

		return arguments;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cardFrontSideText = getArguments().getString(FragmentArguments.CARD_FRONT_SIDE_TEXT);
		cardBackSideText = getArguments().getString(FragmentArguments.CARD_BACK_SIDE_TEXT);
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_card, fragmentContainer, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpCardClickListener();

		setCurrentCardText();
	}

	private void setUpCardClickListener() {
		TextView cardTextView = (TextView) getView().findViewById(R.id.text);

		cardTextView.setOnClickListener(this);
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

		setCurrentCardText();
	}

	private void setCurrentCardText() {
		switch (currentCardSide) {
			case FRONT:
				setCardText(cardFrontSideText);
				break;

			case BACK:
				setCardText(cardBackSideText);
				break;

			default:
				setCardText(cardFrontSideText);
				break;
		}
	}

	private void setCardText(String text) {
		TextView cardTextView = (TextView) getView().findViewById(R.id.text);

		cardTextView.setText(text);
	}
}
