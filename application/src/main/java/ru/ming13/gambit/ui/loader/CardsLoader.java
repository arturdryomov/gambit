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
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.gambit.local.Card;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.loader.result.LoaderResult;
import ru.ming13.gambit.ui.loader.result.LoaderStatus;


public class CardsLoader extends AsyncTaskLoader<LoaderResult<List<Card>>>
{
	private final Deck deck;

	public static CardsLoader newInstance(Context context, Deck deck) {
		return new CardsLoader(context, deck);
	}

	private CardsLoader(Context context, Deck deck) {
		super(context);

		this.deck = deck;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public LoaderResult<List<Card>> loadInBackground() {
		List<Card> cards = deck.getCardsList();

		return buildSuccessResult(cards);
	}

	private LoaderResult<List<Card>> buildSuccessResult(List<Card> cards) {
		return new LoaderResult<List<Card>>(LoaderStatus.SUCCESS, cards, new String());
	}
}
