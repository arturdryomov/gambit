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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.ming13.gambit.R;
import ru.ming13.gambit.backup.BackupFilePicker;
import ru.ming13.gambit.backup.BackupOperator;
import ru.ming13.gambit.bus.BackupFinishedEvent;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.provider.GambitContract;
import ru.ming13.gambit.task.BackupExportingTask;
import ru.ming13.gambit.task.BackupImportingTask;
import ru.ming13.gambit.util.GoogleServices;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.ViewDirector;

public class BackupActivity extends ActionBarActivity implements ResultCallback<DriveApi.DriveContentsResult>,
	GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener
{
	private static enum BackupAction
	{
		EXPORT, IMPORT, NONE
	}

	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	private GoogleApiClient googleApiClient;
	private BackupAction backupAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);

		setUpInjections();

		setUpToolbar();
	}

	private void setUpInjections() {
		ButterKnife.inject(this);
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@OnClick(R.id.button_export)
	public void setUpBackupExport() {
		startBackupAction(BackupAction.EXPORT);
	}

	@OnClick(R.id.button_import)
	public void setUpBackupImport() {
		startBackupAction(BackupAction.IMPORT);
	}

	private void startBackupAction(BackupAction backupAction) {
		this.backupAction = backupAction;

		setUpGoogleApiClient();
		setUpGoogleApiConnection();
	}

	private void setUpGoogleApiClient() {
		this.googleApiClient = buildGoogleApiClient();
	}

	private GoogleApiClient buildGoogleApiClient() {
		return new GoogleApiClient.Builder(this)
			.addApi(Drive.API)
			.addScope(Drive.SCOPE_FILE)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();
	}

	private void setUpGoogleApiConnection() {
		googleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		startBackupAction();
	}

	private void startBackupAction() {
		showProgress();

		startFilesSync();

		switch (backupAction) {
			case EXPORT:
				startBackupFileCreation();
				break;

			case IMPORT:
				startBackupFileOpening();
				break;

			default:
				break;
		}
	}

	private void showProgress() {
		ViewDirector.of(this, R.id.animator).show(R.id.progress);
	}

	private void startFilesSync() {
		Drive.DriveApi.requestSync(googleApiClient).setResultCallback(null);
	}

	private void startBackupFileCreation() {
		Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(this);
	}

	@Override
	public void onResult(DriveApi.DriveContentsResult contentsResult) {
		continueBackupFileCreation(contentsResult.getDriveContents());
	}

	private void continueBackupFileCreation(DriveContents backupFileContents) {
		BackupFilePicker.with(this, googleApiClient).startBackupFileCreation(backupFileContents);
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
		ViewDirector.of(this, R.id.animator).show(R.id.layout_buttons);
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
		GoogleServices.with(this).resolve(connectionResult);
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
