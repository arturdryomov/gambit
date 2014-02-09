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

import ru.ming13.gambit.provider.GambitContract;

public class CardsDeletionTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final List<Uri> cardUris;

	public static void execute(ContentResolver contentResolver, List<Uri> cardUris) {
		new CardsDeletionTask(contentResolver, cardUris).execute();
	}

	private CardsDeletionTask(ContentResolver contentResolver, List<Uri> cardUris) {
		this.contentResolver = contentResolver;
		this.cardUris = cardUris;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		deleteCards();

		return null;
	}

	private void deleteCards() {
		try {
			contentResolver.applyBatch(GambitContract.AUTHORITY, buildDeletionOperations());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} catch (OperationApplicationException e) {
			throw new RuntimeException(e);
		}
	}

	private ArrayList<ContentProviderOperation> buildDeletionOperations() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (Uri cardUri : cardUris) {
			operations.add(ContentProviderOperation.newDelete(cardUri).build());
		}

		return operations;
	}
}
