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

package ru.ming13.gambit.backup;

import android.app.Activity;
import android.content.IntentSender;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.MetadataChangeSet;

import ru.ming13.gambit.R;
import ru.ming13.gambit.util.Intents;

public final class BackupFilePicker
{
	private static final String BACKUP_MIME_TYPE = "application/vnd.gambit.backup";

	private final Activity activity;
	private final GoogleApiClient driveApiClient;

	public static BackupFilePicker with(@NonNull Activity activity, @NonNull GoogleApiClient driveApiClient) {
		return new BackupFilePicker(activity, driveApiClient);
	}

	private BackupFilePicker(Activity activity, GoogleApiClient driveApiClient) {
		this.activity = activity;
		this.driveApiClient = driveApiClient;
	}

	public void startBackupFileCreation(@NonNull DriveContents fileContents) {
		try {
			IntentSender intentSender = buildBackupFileCreationIntentSender(fileContents);

			activity.startIntentSenderForResult(intentSender, Intents.Requests.DRIVE_FILE_CREATE, null, 0, 0, 0);
		} catch (IntentSender.SendIntentException e) {
			throw new RuntimeException(e);
		}
	}

	private IntentSender buildBackupFileCreationIntentSender(DriveContents fileContents) {
		MetadataChangeSet fileMetadata = new MetadataChangeSet.Builder()
			.setTitle(activity.getString(R.string.name_backup))
			.setMimeType(BACKUP_MIME_TYPE)
			.build();

		return Drive.DriveApi.newCreateFileActivityBuilder()
			.setInitialMetadata(fileMetadata)
			.setInitialDriveContents(fileContents)
			.build(driveApiClient);
	}

	public void startBackupFileOpening() {
		try {
			IntentSender intentSender = buildBackupFileOpeningIntentSender();

			activity.startIntentSenderForResult(intentSender, Intents.Requests.DRIVE_FILE_OPEN, null, 0, 0, 0);
		} catch (IntentSender.SendIntentException e) {
			throw new RuntimeException(e);
		}
	}

	private IntentSender buildBackupFileOpeningIntentSender() {
		return Drive.DriveApi.newOpenFileActivityBuilder()
			.setMimeType(new String[]{BACKUP_MIME_TYPE})
			.build(driveApiClient);
	}
}
