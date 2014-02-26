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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;

public class CardsDeletionTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final Deck deck;
	private final List<Card> cards;

	public static void execute(ContentResolver contentResolver, Deck deck, List<Card> cards) {
		new CardsDeletionTask(contentResolver, deck, cards).execute();
	}

	private CardsDeletionTask(ContentResolver contentResolver, Deck deck, List<Card> cards) {
		this.contentResolver = contentResolver;
		this.deck = deck;
		this.cards = cards;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		deleteCards();

		return null;
	}

	private void deleteCards() {
		try {
			contentResolver.applyBatch(GambitContract.AUTHORITY, buildCardsDeletionOperations());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} catch (OperationApplicationException e) {
			throw new RuntimeException(e);
		}
	}

	private ArrayList<ContentProviderOperation> buildCardsDeletionOperations() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (Card card : cards) {
			operations.add(ContentProviderOperation.newDelete(buildCardUri(card)).build());
		}

		return operations;
	}

	private Uri buildCardUri(Card card) {
		return GambitContract.Cards.getCardUri(deck.getId(), card.getId());
	}
}
