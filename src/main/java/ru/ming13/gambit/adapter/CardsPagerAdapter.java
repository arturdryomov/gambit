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


import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.support.v13.app.FragmentStatePagerAdapter;

import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.fragment.CardEmptyFragment;
import ru.ming13.gambit.fragment.CardFragment;


public class CardsPagerAdapter extends FragmentStatePagerAdapter
{
	private Cursor cardsCursor;

	public CardsPagerAdapter(FragmentManager fragmentManager, Cursor cardsCursor) {
		super(fragmentManager);

		this.cardsCursor = cardsCursor;
	}

	@Override
	public int getCount() {
		if (cardsCursor == null) {
			return 0;
		}

		if (cardsCursor.getCount() == 0) {
			return 1;
		}

		return cardsCursor.getCount();
	}

	@Override
	public Fragment getItem(int position) {
		if (cardsCursor == null) {
			return null;
		}

		if (cardsCursor.getCount() == 0) {
			return CardEmptyFragment.newInstance();
		}

		return CardFragment.newInstance(getCard(position));
	}

	private Card getCard(int position) {
		cardsCursor.moveToPosition(position);

		String cardFrontSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
		String cardBackSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));

		return new Card(cardFrontSideText, cardBackSideText);
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

	@Override
	public int getItemPosition(Object object) {
		// This is a really dirty hack.
		// Unfortunatelly I cannot find any nice method to force items redraw.

		return POSITION_NONE;
	}
}
