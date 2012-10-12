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

package ru.ming13.gambit.ui.task;


import android.os.AsyncTask;
import ru.ming13.gambit.local.model.Card;
import ru.ming13.gambit.local.model.Deck;


public class CardDeletionTask extends AsyncTask<Void, Void, Void>
{
	private final Deck deck;
	private final Card card;

	public static CardDeletionTask newInstance(Deck deck, Card card) {
		return new CardDeletionTask(deck, card);
	}

	private CardDeletionTask(Deck deck, Card card) {
		this.deck = deck;
		this.card = card;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		deck.deleteCard(card);

		return null;
	}
}
