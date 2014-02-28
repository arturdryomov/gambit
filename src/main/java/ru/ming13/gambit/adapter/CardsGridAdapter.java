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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.ming13.gambit.R;
import ru.ming13.gambit.provider.GambitContract;

public class CardsGridAdapter extends CursorAdapter
{
	private final LayoutInflater layoutInflater;

	public CardsGridAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cardsCursor, ViewGroup viewGroup) {
		return buildCardView(viewGroup);
	}

	private View buildCardView(ViewGroup viewGroup) {
		return layoutInflater.inflate(R.layout.view_card_grid, viewGroup, false);
	}

	@Override
	public void bindView(View cardView, Context context, Cursor cardsCursor) {
		setUpCardInformation(context, cardsCursor, cardView);
	}

	private void setUpCardInformation(Context context, Cursor cardsCursor, View cardView) {
		TextView cardTextView = (TextView) cardView.findViewById(R.id.text);

		String cardFrontSideText = getCardFrontSideText(cardsCursor);
		String cardBackSideText = getCardBackSideText(cardsCursor);

		cardTextView.setText(context.getString(R.string.mask_card_list_item, cardFrontSideText, cardBackSideText));
	}

	private String getCardFrontSideText(Cursor cardsCursor) {
		return cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
	}

	private String getCardBackSideText(Cursor cardsCursor) {
		return cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));
	}
}
