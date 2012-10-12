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
import ru.ming13.gambit.local.DbProvider;
import ru.ming13.gambit.local.model.AlreadyExistsException;
import ru.ming13.gambit.local.model.Deck;
import ru.ming13.gambit.ui.loader.result.LoaderResult;


public class DeckOperationLoader extends AsyncLoader<Deck>
{
	private static enum Operation
	{
		CREATE, RENAME
	}

	private final Operation operation;

	private Deck deck;
	private String deckTitle;

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

	@Override
	public LoaderResult<Deck> loadInBackground() {
		switch (operation) {
			case CREATE:
				return createDeck();

			case RENAME:
				return renameDeck();

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
}
