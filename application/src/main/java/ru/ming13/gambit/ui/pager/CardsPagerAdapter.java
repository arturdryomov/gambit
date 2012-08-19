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

package ru.ming13.gambit.ui.pager;


import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ru.ming13.gambit.local.model.Card;
import ru.ming13.gambit.ui.fragment.CardEmptyFragment;
import ru.ming13.gambit.ui.fragment.CardFragment;


public class CardsPagerAdapter extends FragmentStatePagerAdapter
{
	private final List<Card> cards;

	public CardsPagerAdapter(FragmentManager fragmentManager, List<Card> cards) {
		super(fragmentManager);

		this.cards = cards;
	}

	@Override
	public int getCount() {
		if (cards == null) {
			return 0;
		}

		if (cards.isEmpty()) {
			return 1;
		}

		return cards.size();
	}

	@Override
	public Fragment getItem(int position) {
		if (cards.isEmpty()) {
			return CardEmptyFragment.newInstance();
		}

		Card card = cards.get(position);

		return CardFragment.newInstance(card);
	}
}
