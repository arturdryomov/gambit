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


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.gambit.local.Card;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.loader.result.LoaderResult;
import ru.ming13.gambit.ui.loader.result.LoaderStatus;


public class CardOperationLoader extends AsyncTaskLoader<LoaderResult<Card>>
{
	private static enum Operation
	{
		CREATE, MODIFY, DELETE
	}

	private final Operation operation;

	private Deck deck;
	private Card card;
	private String frontSideText;
	private String backSideText;

	public static CardOperationLoader newCreationLoader(Context context, Deck deck, String frontSideText, String backSideText) {
		CardOperationLoader cardOperationLoader = new CardOperationLoader(context, Operation.CREATE);

		cardOperationLoader.deck = deck;
		cardOperationLoader.frontSideText = frontSideText;
		cardOperationLoader.backSideText = backSideText;

		return cardOperationLoader;
	}

	private CardOperationLoader(Context context, Operation operation) {
		super(context);

		this.operation = operation;
	}

	public static CardOperationLoader newModificationLoader(Context context, Card card, String frontSideText, String backSideText) {
		CardOperationLoader cardOperationLoader = new CardOperationLoader(context, Operation.MODIFY);

		cardOperationLoader.card = card;
		cardOperationLoader.frontSideText = frontSideText;
		cardOperationLoader.backSideText = backSideText;

		return cardOperationLoader;
	}

	public static CardOperationLoader newDeletionLoader(Context context, Deck deck, Card card) {
		CardOperationLoader cardOperationLoader = new CardOperationLoader(context, Operation.DELETE);

		cardOperationLoader.deck = deck;
		cardOperationLoader.card = card;

		return cardOperationLoader;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public LoaderResult<Card> loadInBackground() {
		switch (operation) {
			case CREATE:
				return createCard();

			case MODIFY:
				return modifyCard();

			case DELETE:
				return deleteCard();

			default:
				throw new LoaderException();
		}
	}

	private LoaderResult<Card> createCard() {
		card = deck.createCard(frontSideText, backSideText);

		return buildSuccessResult();
	}

	private LoaderResult<Card> buildSuccessResult() {
		return new LoaderResult<Card>(LoaderStatus.SUCCESS, card, new String());
	}

	private LoaderResult<Card> modifyCard() {
		card.setFrontSideText(frontSideText);
		card.setBackSideText(backSideText);

		return buildSuccessResult();
	}

	private LoaderResult<Card> deleteCard() {
		deck.deleteCard(card);

		return buildSuccessResult();
	}
}
