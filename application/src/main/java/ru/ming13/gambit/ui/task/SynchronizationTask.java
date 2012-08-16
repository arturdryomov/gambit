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


import android.os.AsyncTask;
import ru.ming13.gambit.remote.NothingToSyncException;
import ru.ming13.gambit.remote.SyncException;
import ru.ming13.gambit.remote.Synchronizer;
import ru.ming13.gambit.remote.UnauthorizedException;


public class SynchronizationTask extends AsyncTask<Void, Void, Void>
{
	public interface SynchronizationCallback
	{
		public void onSuccessSynchronization();

		public void onFailedSynchronization();

		public void onWrongAuthentication();
	}

	private static enum Result
	{
		SUCCESS, FAIL, WRONG_AUTHENTICATION
	}

	private final SynchronizationCallback synchronizationCallback;

	private final String authToken;
	private final String apiKey;

	private String spreadsheetKey;

	private Result result;

	public static SynchronizationTask newInstance(SynchronizationCallback synchronizationCallback, String authToken, String apiKey) {
		return new SynchronizationTask(synchronizationCallback, authToken, apiKey);
	}

	private SynchronizationTask(SynchronizationCallback synchronizationCallback, String authToken, String apiKey) {
		this.synchronizationCallback = synchronizationCallback;

		this.authToken = authToken;
		this.apiKey = apiKey;

		spreadsheetKey = new String();
	}

	public void setSpreadsheetKey(String spreadsheetKey) {
		this.spreadsheetKey = spreadsheetKey;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		try {
			synchronize();

			result = Result.SUCCESS;
		}
		catch (NothingToSyncException e) {
			result = Result.SUCCESS;
		}
		catch (UnauthorizedException e) {
			result = Result.WRONG_AUTHENTICATION;
		}
		catch (SyncException e) {
			result = Result.FAIL;
		}

		return null;
	}

	private void synchronize() {
		Synchronizer synchronizer = new Synchronizer(authToken, apiKey);

		if (spreadsheetKey.isEmpty()) {
			synchronizer.sync();
		}
		else {
			synchronizer.sync(spreadsheetKey);
		}
	}

	@Override
	protected void onPostExecute(Void resultMessage) {
		super.onPostExecute(resultMessage);

		switch (result) {
			case SUCCESS:
				synchronizationCallback.onSuccessSynchronization();
				break;

			case FAIL:
				synchronizationCallback.onFailedSynchronization();
				break;

			case WRONG_AUTHENTICATION:
				synchronizationCallback.onWrongAuthentication();
				break;

			default:
				synchronizationCallback.onFailedSynchronization();
				break;
		}
	}
}
