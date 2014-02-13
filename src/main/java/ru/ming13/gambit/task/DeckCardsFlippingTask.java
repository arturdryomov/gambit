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
import ru.ming13.gambit.provider.GambitContract;

public class DeckCardsFlippingTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final Uri cardsUri;

	public static void execute(ContentResolver contentResolver, Uri cardsUri) {
		new DeckCardsFlippingTask(contentResolver, cardsUri).execute();
	}

	private DeckCardsFlippingTask(ContentResolver contentResolver, Uri cardsUri) {
		this.contentResolver = contentResolver;
		this.cardsUri = cardsUri;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		Cursor cardsCursor = loadCards();

		List<Uri> cardsUris = getCardsUris(cardsCursor);
		List<Card> cards = getCards(cardsCursor);

		cardsCursor.close();

		flipCards(cardsUris, cards);

		return null;
	}

	private Cursor loadCards() {
		String[] projection = {
			GambitContract.Cards._ID,
			GambitContract.Cards.FRONT_SIDE_TEXT,
			GambitContract.Cards.BACK_SIDE_TEXT};

		String sortOrder = GambitContract.Cards._ID;

		return contentResolver.query(cardsUri, projection, null, null, sortOrder);
	}

	private List<Uri> getCardsUris(Cursor cardsCursor) {
		List<Uri> cardsUris = new ArrayList<Uri>();

		cardsCursor.moveToFirst();
		cardsCursor.moveToPrevious();

		while (cardsCursor.moveToNext()) {
			cardsUris.add(buildCardUri(getCardId(cardsCursor)));
		}

		return cardsUris;
	}

	private Uri buildCardUri(long cardId) {
		return GambitContract.Cards.getCardUri(cardsUri, cardId);
	}

	private long getCardId(Cursor cardsCursor) {
		return cardsCursor.getLong(cardsCursor.getColumnIndex(GambitContract.Cards._ID));
	}

	private List<Card> getCards(Cursor cardsCursor) {
		List<Card> cards = new ArrayList<Card>();

		cardsCursor.moveToFirst();
		cardsCursor.moveToPrevious();

		while (cardsCursor.moveToNext()) {
			cards.add(new Card(getCardFrontSideText(cardsCursor), getCardBackSideText(cardsCursor)));
		}

		return cards;
	}

	private String getCardFrontSideText(Cursor cardsCursor) {
		return cardsCursor.getString(cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
	}

	private String getCardBackSideText(Cursor cardsCursor) {
		return cardsCursor.getString(cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));
	}

	private void flipCards(List<Uri> cardsUris, List<Card> cards) {
		try {
			contentResolver.applyBatch(GambitContract.AUTHORITY, buildCardsFlippingOperations(cardsUris, cards));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} catch (OperationApplicationException e) {
			throw new RuntimeException(e);
		}
	}

	private ArrayList<ContentProviderOperation> buildCardsFlippingOperations(List<Uri> cardsUris, List<Card> cards) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (int cardIndex = 0; cardIndex < cardsUris.size(); cardIndex++) {
			Uri cardUri = cardsUris.get(cardIndex);
			Card card = cards.get(cardIndex);

			operations.add(buildCardFlippingOperation(cardUri, card));
		}

		return operations;
	}

	private ContentProviderOperation buildCardFlippingOperation(Uri cardUri, Card card) {
		return ContentProviderOperation.newUpdate(cardUri)
			.withValue(GambitContract.Cards.FRONT_SIDE_TEXT, card.getBackSideText())
			.withValue(GambitContract.Cards.BACK_SIDE_TEXT, card.getFrontSideText())
			.build();
	}
}
