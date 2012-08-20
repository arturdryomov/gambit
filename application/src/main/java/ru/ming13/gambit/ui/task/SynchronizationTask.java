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


import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import org.apache.commons.lang.StringUtils;
import ru.ming13.gambit.remote.NothingToSyncException;
import ru.ming13.gambit.remote.SyncException;
import ru.ming13.gambit.remote.Synchronizer;
import ru.ming13.gambit.remote.drive.UnauthorizedException;
import ru.ming13.gambit.ui.util.Preferences;


public class SynchronizationTask extends AsyncTask<Void, Void, String>
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

	private Context context;

	private SynchronizationCallback synchronizationCallback;

	private final String authToken;
	private final String apiKey;

	private Result result;

	public static SynchronizationTask newInstance(Context context, SynchronizationCallback synchronizationCallback, String authToken, String apiKey) {
		return new SynchronizationTask(context, synchronizationCallback, authToken, apiKey);
	}

	private SynchronizationTask(Context context, SynchronizationCallback synchronizationCallback, String authToken, String apiKey) {
		this.context = context;

		this.synchronizationCallback = synchronizationCallback;

		this.authToken = authToken;
		this.apiKey = apiKey;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setSynchronizationCallback(SynchronizationCallback synchronizationCallback) {
		this.synchronizationCallback = synchronizationCallback;
	}

	@Override
	protected String doInBackground(Void... parameters) {
		try {
			String spreadsheetKey = synchronize();

			result = Result.SUCCESS;

			return spreadsheetKey;
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

		return StringUtils.EMPTY;
	}

	private String synchronize() {
		Synchronizer synchronizer = new Synchronizer(authToken, apiKey);

		if (haveSpreadsheetKey()) {
			return synchronizer.sync(loadSpreadsheetKey());
		}
		else {
			return synchronizer.sync();
		}
	}

	private boolean haveSpreadsheetKey() {
		return !TextUtils.isEmpty(loadSpreadsheetKey());
	}

	private String loadSpreadsheetKey() {
		return Preferences.getString(context, Preferences.Keys.SYNC_SPREADSHEET_KEY);
	}

	@Override
	protected void onPostExecute(String spreadsheetKey) {
		super.onPostExecute(spreadsheetKey);

		switch (result) {
			case SUCCESS:
				saveSpreadsheetKey(spreadsheetKey);

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

	private void saveSpreadsheetKey(String spreadsheetKey) {
		Preferences.set(context, Preferences.Keys.SYNC_SPREADSHEET_KEY, spreadsheetKey);
	}
}
