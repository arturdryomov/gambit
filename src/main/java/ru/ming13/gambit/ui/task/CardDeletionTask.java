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

package ru.ming13.gambit.ui.task;


import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;


public class CardDeletionTask extends AsyncTask<Void, Void, Void>
{
	private final ContentResolver contentResolver;
	private final Uri cardUri;

	public static void execute(ContentResolver contentResolver, Uri cardUri) {
		new CardDeletionTask(contentResolver, cardUri).execute();
	}

	private CardDeletionTask(ContentResolver contentResolver, Uri cardUri) {
		this.contentResolver = contentResolver;
		this.cardUri = cardUri;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		deleteCard();

		return null;
	}

	private void deleteCard() {
		contentResolver.delete(cardUri, null, null);
	}
}
