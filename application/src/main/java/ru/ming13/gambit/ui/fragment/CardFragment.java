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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.Card;


public class CardFragment extends SherlockFragment
{
	private static enum CardSide
	{
		FRONT, BACK
	}

	private Card card;
	private CardSide currentCardSide = CardSide.FRONT;

	public static CardFragment newInstance(Card card) {
		CardFragment cardFragment = new CardFragment();

		cardFragment.setArguments(buildArguments(card));

		return cardFragment;
	}

	private static Bundle buildArguments(Card card) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(FragmentArguments.CARD, card);

		return arguments;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		card = getArguments().getParcelable(FragmentArguments.CARD);
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

		cardTextView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				flipCard();
			}
		});
	}

	private void flipCard() {
		switch (currentCardSide) {
			case FRONT:
				currentCardSide = CardSide.BACK;
				break;

			case BACK:
				currentCardSide = CardSide.FRONT;
				break;
		}

		setCurrentCardText();
	}

	private void setCurrentCardText() {
		switch (currentCardSide) {
			case FRONT:
				setCardText(card.getFrontSideText());
				break;

			case BACK:
				setCardText(card.getBackSideText());
				break;
		}
	}

	private void setCardText(String text) {
		TextView cardTextView = (TextView) getView().findViewById(R.id.text);

		cardTextView.setText(text);
	}
}
