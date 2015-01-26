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
import ru.ming13.gambit.model.Deck;

public class DecksListAdapter extends IterableCursorAdapter<Deck>
{
	static final class DeckViewHolder
	{
		@InjectView(R.id.text)
		public TextView deckTitle;

		public DeckViewHolder(View deckView) {
			ButterKnife.inject(this, deckView);
		}
	}

	private final LayoutInflater layoutInflater;

	public DecksListAdapter(Context context) {
		super(context, null, 0);

		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Deck deck, ViewGroup deckViewContainer) {
		View deckView = layoutInflater.inflate(R.layout.view_list_item, deckViewContainer, false);

		deckView.setTag(new DeckViewHolder(deckView));

		return deckView;
	}

	@Override
	public void bindView(View deckView, Context context, Deck deck) {
		DeckViewHolder deckViewHolder = (DeckViewHolder) deckView.getTag();

		deckViewHolder.deckTitle.setText(deck.getTitle());
	}
}
