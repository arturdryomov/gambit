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

package ru.ming13.gambit.task;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;

public class DeckCardsFlippingTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final Deck deck;

	public static void execute(ContentResolver contentResolver, Deck deck) {
		new DeckCardsFlippingTask(contentResolver, deck).execute();
	}

	private DeckCardsFlippingTask(ContentResolver contentResolver, Deck deck) {
		this.contentResolver = contentResolver;
		this.deck = deck;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		Cursor cardsCursor = loadCards();

		List<Card> cards = getCards(cardsCursor);

		cardsCursor.close();

		flipCards(cards);

		return null;
	}

	private Cursor loadCards() {
		String sortOrder = GambitContract.Cards._ID;

		return contentResolver.query(buildCardsUri(), null, null, null, sortOrder);
	}

	private Uri buildCardsUri() {
		return GambitContract.Cards.getCardsUri(deck.getId());
	}

	private List<Card> getCards(Cursor cardsCursor) {
		List<Card> cards = new ArrayList<Card>();

		cardsCursor.moveToFirst();
		cardsCursor.moveToPrevious();

		while (cardsCursor.moveToNext()) {
			cards.add(getCard(cardsCursor));
		}

		return cards;
	}

	private Card getCard(Cursor cardsCursor) {
		long cardId = cardsCursor.getLong(
			cardsCursor.getColumnIndex(GambitContract.Cards._ID));
		String cardFrontSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
		String cardBackSideText = cardsCursor.getString(
			cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));

		return new Card(cardId, cardFrontSideText, cardBackSideText);
	}

	private void flipCards(List<Card> cards) {
		try {
			contentResolver.applyBatch(GambitContract.AUTHORITY, buildCardsFlippingOperations(cards));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} catch (OperationApplicationException e) {
			throw new RuntimeException(e);
		}
	}

	private ArrayList<ContentProviderOperation> buildCardsFlippingOperations(List<Card> cards) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (Card card : cards) {
			operations.add(buildCardFlippingOperation(card));
		}

		return operations;
	}

	private ContentProviderOperation buildCardFlippingOperation(Card card) {
		return ContentProviderOperation.newUpdate(buildCardUri(card))
			.withValue(GambitContract.Cards.FRONT_SIDE_TEXT, card.getBackSideText())
			.withValue(GambitContract.Cards.BACK_SIDE_TEXT, card.getFrontSideText())
			.build();
	}

	private Uri buildCardUri(Card card) {
		return GambitContract.Cards.getCardUri(deck.getId(),card.getId());
	}
}
