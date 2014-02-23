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

package ru.ming13.gambit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewAnimator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.squareup.otto.Subscribe;

import ru.ming13.gambit.R;
import ru.ming13.gambit.backup.BackupFilePicker;
import ru.ming13.gambit.backup.BackupOperator;
import ru.ming13.gambit.bus.BackupFinishedEvent;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.BackupExportingTask;
import ru.ming13.gambit.task.BackupImportingTask;
import ru.ming13.gambit.util.GoogleServicesUtil;
import ru.ming13.gambit.util.Intents;

public class BackupActivity extends Activity implements View.OnClickListener,
	GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener,
	ResultCallback<DriveApi.ContentsResult>
{
	private static enum BackupAction
	{
		EXPORT, IMPORT, NONE
	}

	private GoogleApiClient googleApiClient;
	private BackupAction backupAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);

		setUpButtons();
	}

	private void setUpButtons() {
		findViewById(R.id.button_export).setOnClickListener(this);
		findViewById(R.id.button_import).setOnClickListener(this);
	}

	@Override
	public void onClick(View button) {
		switch (button.getId()) {
			case R.id.button_export:
				startBackupAction(BackupAction.EXPORT);
				break;

			case R.id.button_import:
				startBackupAction(BackupAction.IMPORT);
				break;

			default:
				break;
		}
	}

	private void startBackupAction(BackupAction backupAction) {
		this.backupAction = backupAction;

		setUpGoogleApiConnection();
	}

	private void setUpGoogleApiConnection() {
		this.googleApiClient = buildGoogleApiClient();

		googleApiClient.connect();
	}

	private GoogleApiClient buildGoogleApiClient() {
		return new GoogleApiClient.Builder(this)
			.addApi(Drive.API)
			.addScope(Drive.SCOPE_FILE)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		startBackupAction();
	}

	private void startBackupAction() {
		showProgress();

		switch (backupAction) {
			case EXPORT:
				startBackupFileCreation();
				break;

			case IMPORT:
				startBackupFileOpening();
				break;

			default:
				throw new RuntimeException();
		}
	}

	private void showProgress() {
		ViewAnimator animator = (ViewAnimator) findViewById(R.id.animator);
		animator.setDisplayedChild(animator.indexOfChild(findViewById(R.id.progress)));
	}

	private void startBackupFileCreation() {
		Drive.DriveApi.newContents(googleApiClient).setResultCallback(this);
	}

	@Override
	public void onResult(DriveApi.ContentsResult contentsResult) {
		continueBackupFileCreation(contentsResult.getContents());
	}

	private void continueBackupFileCreation(Contents backupContents) {
		BackupFilePicker.with(this, googleApiClient).startBackupFileCreation(backupContents);
	}

	private void startBackupFileOpening() {
		BackupFilePicker.with(this, googleApiClient).startBackupFileOpening();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) {
			finishBackupAction();
			return;
		}

		if (requestCode == Intents.Requests.GOOGLE_CONNECTION) {
			setUpGoogleApiConnection();
		}

		if (requestCode == Intents.Requests.DRIVE_FILE_CREATE) {
			DriveId backupFileId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
			startBackupExporting(backupFileId);
		}

		if (requestCode == Intents.Requests.DRIVE_FILE_OPEN) {
			DriveId backupFileId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
			startBackupImporting(backupFileId);
		}
	}

	private void finishBackupAction() {
		this.backupAction = BackupAction.NONE;

		hideProgress();

		showUpdatedContents();

		tearDownGoogleApiConnection();
	}

	private void hideProgress() {
		ViewAnimator animator = (ViewAnimator) findViewById(R.id.animator);
		animator.setDisplayedChild(animator.indexOfChild(findViewById(R.id.layout_buttons)));
	}

	private void showUpdatedContents() {
		getContentResolver().notifyChange(GambitContract.Decks.getDecksUri(), null);
	}

	private void tearDownGoogleApiConnection() {
		if (isGoogleApiClientConnected()) {
			googleApiClient.disconnect();
		}
	}

	private boolean isGoogleApiClientConnected() {
		return (googleApiClient != null) && (googleApiClient.isConnecting() || googleApiClient.isConnected());
	}

	private void startBackupExporting(DriveId backupFileId) {
		BackupExportingTask.execute(BackupOperator.with(this, googleApiClient), backupFileId);
	}

	private void startBackupImporting(DriveId backupFileId) {
		BackupImportingTask.execute(BackupOperator.with(this, googleApiClient), backupFileId);
	}

	@Subscribe
	public void onBackupFinished(BackupFinishedEvent event) {
		finishBackupAction();
	}

	@Override
	public void onConnectionSuspended(int cause) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		GoogleServicesUtil.with(this).resolve(connectionResult);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				navigateUp();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void navigateUp() {
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		tearDownGoogleApiConnection();
	}
}
