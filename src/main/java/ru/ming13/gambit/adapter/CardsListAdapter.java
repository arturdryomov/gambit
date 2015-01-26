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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venmo.cursor.IterableCursorAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.gambit.R;
import ru.ming13.gambit.model.Card;

public class CardsListAdapter extends IterableCursorAdapter<Card>
{
	static final class CardViewHolder
	{
		@InjectView(R.id.text)
		public TextView cardText;

		public CardViewHolder(View cardView) {
			ButterKnife.inject(this, cardView);
		}
	}

	private final LayoutInflater layoutInflater;

	public CardsListAdapter(Context context) {
		super(context, null, 0);

		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Card card, ViewGroup cardViewContainer) {
		View cardView = layoutInflater.inflate(R.layout.view_card_list, cardViewContainer, false);

		cardView.setTag(new CardViewHolder(cardView));

		return cardView;
	}

	@Override
	public void bindView(View cardView, Context context, Card card) {
		CardViewHolder cardViewHolder = (CardViewHolder) cardView.getTag();

		cardViewHolder.cardText.setText(context.getString(R.string.mask_card_list_item, card.getFrontSideText(), card.getBackSideText()));
	}
}
