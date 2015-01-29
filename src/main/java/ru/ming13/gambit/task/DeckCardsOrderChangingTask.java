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

import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;

abstract class DeckCardsOrderChangingTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final Deck deck;

	protected DeckCardsOrderChangingTask(ContentResolver contentResolver, Deck deck) {
		this.contentResolver = contentResolver;
		this.deck = deck;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		changeCardsOrder();

		return null;
	}

	private void changeCardsOrder() {
		try {
			List<Uri> cardsUris = getCardsUris();
			List<Integer> cardsOrderIndices = buildCardsOrderIndices(cardsUris.size());

			contentResolver.applyBatch(GambitContract.AUTHORITY, buildChangingOrderOperations(cardsUris, cardsOrderIndices));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}

	private List<Uri> getCardsUris() {
		List<Uri> cardsUris = new ArrayList<>();

		Cursor cardsCursor = loadCards();

		while (cardsCursor.moveToNext()) {
			cardsUris.add(buildCardUri(getCardId(cardsCursor)));
		}

		cardsCursor.close();

		return cardsUris;
	}

	private Cursor loadCards() {
		String[] projection = {GambitContract.Cards._ID};

		return contentResolver.query(buildCardsUri(), projection, null, null, null);
	}

	private Uri buildCardsUri() {
		return GambitContract.Cards.getCardsUri(deck.getId());
	}

	private Uri buildCardUri(long cardId) {
		return GambitContract.Cards.getCardUri(deck.getId(), cardId);
	}

	private long getCardId(Cursor cardsCursor) {
		return cardsCursor.getLong(cardsCursor.getColumnIndex(GambitContract.Cards._ID));
	}

	protected abstract List<Integer> buildCardsOrderIndices(int indicesCount);

	private ArrayList<ContentProviderOperation> buildChangingOrderOperations(List<Uri> cardsUris, List<Integer> cardsOrderIndices) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<>();

		for (int cardIndex = 0; cardIndex < cardsUris.size(); cardIndex++) {
			Uri cardUri = cardsUris.get(cardIndex);
			Integer cardOrderIndex = cardsOrderIndices.get(cardIndex);

			operations.add(buildChangingOrderOperation(cardUri, cardOrderIndex));
		}

		return operations;
	}

	private ContentProviderOperation buildChangingOrderOperation(Uri cardUri, Integer cardOrderIndex) {
		return ContentProviderOperation.newUpdate(cardUri)
			.withValue(GambitContract.Cards.ORDER_INDEX, cardOrderIndex)
			.build();
	}
}
