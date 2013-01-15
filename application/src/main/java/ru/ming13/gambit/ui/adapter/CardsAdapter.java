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


import android.content.Context;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.model.Card;


public class CardsAdapter extends OneLineListAdapter<Card>
{
	public CardsAdapter(Context context) {
		super(context);
	}

	@Override
	protected String buildListItemText(int position) {
		Card card = getItem(position);
		String maskCardListItem = getContext().getString(R.string.mask_card_list_item);

		return String.format(maskCardListItem, card.getFrontSideText(), card.getBackSideText());
	}
}
