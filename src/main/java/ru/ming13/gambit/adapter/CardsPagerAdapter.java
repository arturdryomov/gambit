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

package ru.ming13.gambit.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ming13.gambit.R;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.provider.GambitContract;

public class CardsPagerAdapter extends PagerAdapter implements View.OnClickListener
{
	private static enum CardSide
	{
		FRONT, BACK
	}

	private final LayoutInflater layoutInflater;

	private Cursor cardsCursor;
	private CardSide defaultCardSide = CardSide.FRONT;

	public CardsPagerAdapter(Context context) {
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public Object instantiateItem(ViewGroup cardsPagerContainer, int cardPosition) {
		ViewPager cardsPager = getCardsPager(cardsPagerContainer);
		View cardView = getCardView(cardsPager);

		setUpCardInformation(cardView, getCard(cardPosition), defaultCardSide);
		setUpCardText(cardView);
		setUpCardListener(cardView);

		cardsPager.addView(cardView);
		return cardView;
	}

	private ViewPager getCardsPager(ViewGroup cardsPagerContainer) {
		return (ViewPager) cardsPagerContainer;
	}

	private View getCardView(ViewPager cardsPager) {
		return layoutInflater.inflate(R.layout.view_card_pager, cardsPager, false);
	}

	private void setUpCardInformation(View cardView, Card card, CardSide cardSide) {
		cardView.setTag(Pair.create(card, cardSide));
	}

	private Card getCard(int cardPosition) {
		cardsCursor.moveToPosition(cardPosition);

		String cardFrontSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
		String cardBackSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));

		return new Card(cardFrontSideText, cardBackSideText);
	}

	private void setUpCardText(View cardView) {
		if (getCardSide(cardView) == CardSide.FRONT) {
			getCardTextView(cardView).setText(getCard(cardView).getFrontSideText());
		} else {
			getCardTextView(cardView).setText(getCard(cardView).getBackSideText());
		}
	}

	private CardSide getCardSide(View cardView) {
		return getCardInformation(cardView).second;
	}

	@SuppressWarnings("unchecked")
	private Pair<Card, CardSide> getCardInformation(View cardView) {
		return (Pair<Card, CardSide>) cardView.getTag();
	}

	private TextView getCardTextView(View cardView) {
		return (TextView) cardView.findViewById(R.id.text);
	}

	private Card getCard(View cardView) {
		return getCardInformation(cardView).first;
	}

	private void setUpCardListener(View cardView) {
		cardView.setOnClickListener(this);
	}

	@Override
	public void onClick(View cardView) {
		Card card = getCard(cardView);
		CardSide cardSide = getFlippedCardSide(getCardSide(cardView));

		setUpCardInformation(cardView, card, cardSide);
		setUpCardText(cardView);
	}

	private CardSide getFlippedCardSide(CardSide cardSide) {
		if (cardSide == CardSide.FRONT) {
			return CardSide.BACK;
		} else {
			return CardSide.FRONT;
		}
	}

	@Override
	public void destroyItem(ViewGroup cardsPagerContainer, int cardPosition, Object cardViewObject) {
		getCardsPager(cardsPagerContainer).removeView((View) cardViewObject);
	}

	@Override
	public boolean isViewFromObject(View cardView, Object cardViewObject) {
		return cardView.equals(cardViewObject);
	}

	@Override
	public int getItemPosition(Object cardViewObject) {
		return PagerAdapter.POSITION_NONE;
	}

	@Override
	public int getCount() {
		if (cardsCursor == null) {
			return 0;
		} else {
			return cardsCursor.getCount();
		}
	}

	public boolean isEmpty() {
		return getCount() == 0;
	}

	public void swapCursor(Cursor cardsCursor) {
		if (this.cardsCursor == cardsCursor) {
			return;
		}

		this.cardsCursor = cardsCursor;

		if (this.cardsCursor != null) {
			notifyDataSetChanged();
		}
	}

	public void switchDefaultCardSide() {
		defaultCardSide = getFlippedCardSide(defaultCardSide);
	}
}
