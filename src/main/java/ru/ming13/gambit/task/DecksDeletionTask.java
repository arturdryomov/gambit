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

import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;

public class DecksDeletionTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final List<Deck> decks;

	public static void execute(ContentResolver contentResolver, List<Deck> decks) {
		new DecksDeletionTask(contentResolver, decks).execute();
	}

	private DecksDeletionTask(ContentResolver contentResolver, List<Deck> decks) {
		this.contentResolver = contentResolver;
		this.decks = decks;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		deleteDecks();

		return null;
	}

	private void deleteDecks() {
		try {
			contentResolver.applyBatch(GambitContract.AUTHORITY, buildDecksDeletionOperations());
		} catch (RemoteException | OperationApplicationException e) {
			throw new RuntimeException(e);
		}
	}

	private ArrayList<ContentProviderOperation> buildDecksDeletionOperations() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<>();

		for (Deck deck : decks) {
			operations.add(ContentProviderOperation.newDelete(buildDeckUri(deck)).build());
		}

		return operations;
	}

	private Uri buildDeckUri(Deck deck) {
		return GambitContract.Decks.getDeckUri(deck.getId());
	}
}
