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


import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import ru.ming13.gambit.provider.BatchApplyingException;
import ru.ming13.gambit.provider.GambitContract;


abstract class DeckCardsOrderChangingTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final Uri cardsUri;

	protected DeckCardsOrderChangingTask(ContentResolver contentResolver, Uri cardsUri) {
		this.contentResolver = contentResolver;
		this.cardsUri = cardsUri;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		List<Uri> cardsUris = getCardsUris();
		List<Integer> cardsOrderIndices = buildCardsOrderIndices(cardsUris.size());

		applyOperations(buildChangingOrderOperations(cardsUris, cardsOrderIndices));

		return null;
	}

	private List<Uri> getCardsUris() {
		List<Uri> cardsUris = new ArrayList<Uri>();

		Cursor cardsCursor = queryCards();

		while (cardsCursor.moveToNext()) {
			cardsUris.add(buildCardUri(cardsCursor));
		}

		cardsCursor.close();

		return cardsUris;
	}

	private Cursor queryCards() {
		String[] projection = {GambitContract.Cards._ID};

		return contentResolver.query(cardsUri, projection, null, null, null);
	}

	private Uri buildCardUri(Cursor cardsCursor) {
		return GambitContract.Cards.buildCardUri(cardsUri, extractCardId(cardsCursor));
	}

	private long extractCardId(Cursor cardsCursor) {
		return cardsCursor.getLong(cardsCursor.getColumnIndex(GambitContract.Cards._ID));
	}

	protected abstract List<Integer> buildCardsOrderIndices(int indicesCount);

	private ArrayList<ContentProviderOperation> buildChangingOrderOperations(List<Uri> cardsUris, List<Integer> cardsOrderIndices) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (int cardIndex = 0; cardIndex < cardsUris.size(); cardIndex++) {
			Uri cardUri = cardsUris.get(cardIndex);
			Integer cardOrderIndex = cardsOrderIndices.get(cardIndex);

			operations.add(buildChangingOrderOperation(cardUri, cardOrderIndex));
		}

		return operations;
	}

	private ContentProviderOperation buildChangingOrderOperation(Uri cardUri, Integer cardOrderIndex) {
		return ContentProviderOperation.newUpdate(cardUri).withValue(GambitContract.Cards.ORDER_INDEX,
			cardOrderIndex).build();
	}

	private void applyOperations(ArrayList<ContentProviderOperation> operations) {
		try {
			contentResolver.applyBatch(GambitContract.AUTHORITY, operations);
		}
		catch (RemoteException e) {
			throw new BatchApplyingException();
		}
		catch (OperationApplicationException e) {
			throw new BatchApplyingException();
		}
	}
}
