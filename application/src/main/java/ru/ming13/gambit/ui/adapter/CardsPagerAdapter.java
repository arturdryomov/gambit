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

package ru.ming13.gambit.ui.adapter;


import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.ui.fragment.CardEmptyFragment;
import ru.ming13.gambit.ui.fragment.CardFragment;


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
		if (cardsCursor.getCount() == 0) {
			return CardEmptyFragment.newInstance();
		}

		cardsCursor.moveToPosition(position);

		return CardFragment.newInstance(extractCardFrontSideText(cardsCursor),
			extractCardBackSideText(cardsCursor));
	}

	private String extractCardFrontSideText(Cursor cardsCursor) {
		return cardsCursor.getString(cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
	}

	private String extractCardBackSideText(Cursor cardsCursor) {
		return cardsCursor.getString(cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));
	}

	public void swapCursor(Cursor cardsCursor) {
		this.cardsCursor = cardsCursor;
	}

	public boolean isEmpty() {
		return (cardsCursor == null) || (cardsCursor.getCount() == 0);
	}
}
