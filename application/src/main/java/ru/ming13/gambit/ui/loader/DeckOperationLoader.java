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
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.AlreadyExistsException;
import ru.ming13.gambit.local.DbProvider;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.loader.result.LoaderResult;


public class DeckOperationLoader extends AsyncLoader<Deck>
{
	private static enum Operation
	{
		CREATE, RENAME, DELETE, CHANGE_CURRENT_CARD_INDEX
	}

	private final Operation operation;

	private Deck deck;
	private String deckTitle;
	private int currentCardIndex;

	public static DeckOperationLoader newCreationLoader(Context context, String deckTitle) {
		DeckOperationLoader deckOperationLoader = new DeckOperationLoader(context, Operation.CREATE);

		deckOperationLoader.deckTitle = deckTitle;

		return deckOperationLoader;
	}

	private DeckOperationLoader(Context context, Operation operation) {
		super(context);

		this.operation = operation;
	}

	public static DeckOperationLoader newRenamingLoader(Context context, Deck deck, String deckTitle) {
		DeckOperationLoader deckOperationLoader = new DeckOperationLoader(context, Operation.RENAME);

		deckOperationLoader.deck = deck;
		deckOperationLoader.deckTitle = deckTitle;

		return deckOperationLoader;
	}

	public static DeckOperationLoader newDeletionLoader(Context context, Deck deck) {
		DeckOperationLoader deckOperationLoader = new DeckOperationLoader(context, Operation.DELETE);

		deckOperationLoader.deck = deck;

		return deckOperationLoader;
	}

	public static DeckOperationLoader newCurrentCardIndexChangingLoader(Context context, Deck deck, int currentCardIndex) {
		DeckOperationLoader deckOperationLoader = new DeckOperationLoader(context,
			Operation.CHANGE_CURRENT_CARD_INDEX);

		deckOperationLoader.deck = deck;
		deckOperationLoader.currentCardIndex = currentCardIndex;

		return deckOperationLoader;
	}

	@Override
	public LoaderResult<Deck> loadInBackground() {
		switch (operation) {
			case CREATE:
				return createDeck();

			case RENAME:
				return renameDeck();

			case DELETE:
				return deleteDeck();

			case CHANGE_CURRENT_CARD_INDEX:
				return changeCurrentCardIndex();

			default:
				throw new LoaderException();
		}
	}

	private LoaderResult<Deck> createDeck() {
		try {
			deck = DbProvider.getInstance().getDecks().createDeck(deckTitle);

			return buildSuccessResult(deck);
		}
		catch (AlreadyExistsException e) {
			String errorMessage = getContext().getString(R.string.error_deck_already_exists);

			return buildErrorResult(deck, errorMessage);
		}
	}

	private LoaderResult<Deck> renameDeck() {
		try {
			deck.setTitle(deckTitle);

			return buildSuccessResult(deck);
		}
		catch (AlreadyExistsException e) {
			String errorMessage = getContext().getString(R.string.error_deck_already_exists);

			return buildErrorResult(deck, errorMessage);
		}
	}

	private LoaderResult<Deck> deleteDeck() {
		DbProvider.getInstance().getDecks().deleteDeck(deck);

		return buildSuccessResult(deck);
	}

	private LoaderResult<Deck> changeCurrentCardIndex() {
		deck.setCurrentCardIndex(currentCardIndex);

		return buildSuccessResult(deck);
	}
}
