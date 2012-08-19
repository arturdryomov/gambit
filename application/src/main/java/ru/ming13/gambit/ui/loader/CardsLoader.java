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

package ru.ming13.gambit.ui.loader;


import java.util.List;

import android.content.Context;
import ru.ming13.gambit.local.Card;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.loader.result.LoaderResult;


public class CardsLoader extends AsyncLoader<List<Card>>
{
	private static enum Order
	{
		CURRENT, SHUFFLE, ORIGINAL
	}

	private final Order order;

	private final Deck deck;

	public static CardsLoader newCurrentOrderInstance(Context context, Deck deck) {
		return new CardsLoader(context, deck, Order.CURRENT);
	}

	private CardsLoader(Context context, Deck deck, Order order) {
		super(context);

		this.deck = deck;

		this.order = order;
	}

	public static CardsLoader newShuffleOrderInstance(Context context, Deck deck) {
		return new CardsLoader(context, deck, Order.SHUFFLE);
	}

	public static CardsLoader newOriginalOrderInstance(Context context, Deck deck) {
		return new CardsLoader(context, deck, Order.ORIGINAL);
	}

	@Override
	public LoaderResult<List<Card>> loadInBackground() {
		applyOrderToCards();

		List<Card> cards = deck.getCardsList();

		return buildSuccessResult(cards);
	}

	private void applyOrderToCards() {
		switch (order) {
			case CURRENT:
				break;

			case SHUFFLE:
				deck.shuffleCards();
				break;

			case ORIGINAL:
				deck.resetCardsOrder();
				break;
		}
	}
}
