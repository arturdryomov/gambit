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

package ru.ming13.gambit.ui.activity;


import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.view.MenuItem;
import ru.ming13.gambit.R;
import ru.ming13.gambit.ui.fragment.DecksFragment;
import ru.ming13.gambit.ui.fragment.IntermediateProgressDialog;
import ru.ming13.gambit.ui.task.AuthenticationTask;
import ru.ming13.gambit.ui.task.SynchronizationTask;
import ru.ming13.gambit.ui.util.UserAlerter;


public class DecksActivity extends FragmentWrapperActivity implements AuthenticationTask.AuthenticationCallback, SynchronizationTask.SynchronizationCallback
{
	private static final class LastInstanceKeys
	{
		private LastInstanceKeys() {
		}

		public static final String AUTHENTICATION_TASK = "authentication_task";
		public static final String SYNCHRONIZATION_TASK = "syncrhonization_task";
	}

	private AuthenticationTask authenticationTask;
	private SynchronizationTask synchronizationTask;

	@Override
	protected Fragment buildFragment() {
		return DecksFragment.newInstance();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_sync:
				callSynchronization();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void callSynchronization() {
		showSynchronizationProgressDialog();

		authenticationTask = AuthenticationTask.newInstance(this, this);
		authenticationTask.execute();
	}

	private void showSynchronizationProgressDialog() {
		IntermediateProgressDialog progressDialog = IntermediateProgressDialog.newInstance(
			getString(R.string.loading_sync));

		progressDialog.show(getSupportFragmentManager(), IntermediateProgressDialog.TAG);
	}

	@Override
	public void onCancelAuthentication() {
		hideSynchronizationProgressDialog();
	}

	private void hideSynchronizationProgressDialog() {
		IntermediateProgressDialog progressDialog = (IntermediateProgressDialog) getSupportFragmentManager().findFragmentByTag(
			IntermediateProgressDialog.TAG);

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onFailedAuthentication() {
		hideSynchronizationProgressDialog();

		UserAlerter.alert(this, R.string.error_unspecified);
	}

	@Override
	public void onSuccessAuthentication(String authToken) {
		callSynchronization(authToken);
	}

	private void callSynchronization(String authToken) {
		synchronizationTask = SynchronizationTask.newInstance(this, this, authToken, getApiKey());
		synchronizationTask.execute();
	}

	private String getApiKey() {
		return getString(R.string.google_api_key);
	}

	@Override
	public void onFailedSynchronization() {
		hideSynchronizationProgressDialog();

		UserAlerter.alert(this, R.string.error_unspecified);
	}

	@Override
	public void onWrongAuthentication() {
		AuthenticationTask.newInvalidationInstance(this, this).execute();
	}

	@Override
	public void onSuccessSynchronization() {
		repopulateDecks();

		hideSynchronizationProgressDialog();
	}

	private void repopulateDecks() {
		if (isDecksFragmentValid()) {
			getDecksFragment().callListRepopulation();
		}
	}

	private boolean isDecksFragmentValid() {
		return getDecksFragment() != null;
	}

	private DecksFragment getDecksFragment() {
		return (DecksFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		Map<String, Object> instance = new HashMap<String, Object>();

		instance.put(LastInstanceKeys.AUTHENTICATION_TASK, authenticationTask);
		instance.put(LastInstanceKeys.SYNCHRONIZATION_TASK, synchronizationTask);

		return instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		updateRunningTasks();
	}

	private void updateRunningTasks() {
		if (!isLastInstanceValid()) {
			return;
		}

		if (isTaskValid(LastInstanceKeys.AUTHENTICATION_TASK)) {
			updateRunningAuthenticationTask();
		}

		if (isTaskValid(LastInstanceKeys.SYNCHRONIZATION_TASK)) {
			updateRunningSynchronizationTask();
		}
	}

	private boolean isLastInstanceValid() {
		return getLastCustomNonConfigurationInstance() != null;
	}

	private boolean isTaskValid(String lastInstanceKey) {
		Map<String, Object> lastInstance = (Map<String, Object>) getLastCustomNonConfigurationInstance();

		if (!lastInstance.containsKey(lastInstanceKey)) {
			return false;
		}

		return lastInstance.get(lastInstanceKey) != null;
	}

	private void updateRunningAuthenticationTask() {
		Map<String, Object> lastInstance = (Map<String, Object>) getLastCustomNonConfigurationInstance();
		AuthenticationTask authenticationTask = (AuthenticationTask) lastInstance.get(
			LastInstanceKeys.AUTHENTICATION_TASK);

		authenticationTask.setActivity(this);
		authenticationTask.setAuthenticationCallback(this);
	}

	private void updateRunningSynchronizationTask() {
		Map<String, Object> lastInstance = (Map<String, Object>) getLastCustomNonConfigurationInstance();
		SynchronizationTask synchronizationTask = (SynchronizationTask) lastInstance.get(
			LastInstanceKeys.SYNCHRONIZATION_TASK);

		synchronizationTask.setContext(this);
		synchronizationTask.setSynchronizationCallback(this);
	}
}
