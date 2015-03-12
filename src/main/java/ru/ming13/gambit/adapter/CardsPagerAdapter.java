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
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.ming13.gambit.R;
import ru.ming13.gambit.model.Card;

public class CardsPagerAdapter extends PagerAdapter
{
	static final class CardViewHolder
	{
		@InjectView(R.id.text)
		public TextView cardText;

		public Card card;
		public CardSide cardSide;

		public CardViewHolder(View cardView, Card card, CardSide cardSide) {
			this.card = card;
			this.cardSide = cardSide;

			ButterKnife.inject(this, cardView);
		}

		@OnClick(R.id.container_card)
		public void setUpCardSide() {
			this.cardSide = cardSide.flip();

			if (cardSide == CardSide.FRONT) {
				cardText.setText(card.getFrontSideText());
			} else {
				cardText.setText(card.getBackSideText());
			}
		}
	}

	private enum CardSide
	{
		FRONT, BACK;

		public CardSide flip() {
			if (this == FRONT) {
				return BACK;
			} else {
				return FRONT;
			}
		}
	}

	private final LayoutInflater layoutInflater;

	private CardSide defaultCardSide;

	private List<Card> cards;

	public CardsPagerAdapter(@NonNull Context context) {
		this.layoutInflater = LayoutInflater.from(context);

		this.defaultCardSide = CardSide.FRONT;

		this.cards = Collections.emptyList();
	}

	public void refill(@NonNull List<Card> cards) {
		this.cards = cards;

		notifyDataSetChanged();
	}

	public void switchDefaultCardSide() {
		this.defaultCardSide = defaultCardSide.flip();
	}

	@Override
	public Object instantiateItem(ViewGroup cardsPagerContainer, int cardPosition) {
		ViewPager cardsPager = getCardsPager(cardsPagerContainer);

		View cardView = newCardView(cardsPager, cardPosition);
		bindCardView(cardView);

		cardsPager.addView(cardView);

		return cardView;
	}

	private ViewPager getCardsPager(ViewGroup cardsPagerContainer) {
		return (ViewPager) cardsPagerContainer;
	}

	private View newCardView(ViewPager cardsPager, int cardPosition) {
		View cardView = layoutInflater.inflate(R.layout.view_card_pager, cardsPager, false);

		cardView.setTag(new CardViewHolder(cardView, cards.get(cardPosition), defaultCardSide));

		return cardView;
	}

	private void bindCardView(View cardView) {
		CardViewHolder cardViewHolder = (CardViewHolder) cardView.getTag();

		if (cardViewHolder.cardSide == CardSide.FRONT) {
			cardViewHolder.cardText.setText(cardViewHolder.card.getFrontSideText());
		} else {
			cardViewHolder.cardText.setText(cardViewHolder.card.getBackSideText());
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
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}

	@Override
	public int getCount() {
		return cards.size();
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}
}
