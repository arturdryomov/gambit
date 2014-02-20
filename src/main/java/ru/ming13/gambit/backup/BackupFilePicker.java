package ru.ming13.gambit.backup;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.MetadataChangeSet;

import ru.ming13.gambit.R;
import ru.ming13.gambit.util.Intents;

public final class BackupFilePicker
{
	private static final String BACKUP_MIME_TYPE = "application/vnd.gambit.backup";

	private final Activity activity;
	private final GoogleApiClient driveApiClient;

	public static BackupFilePicker with(Activity activity, GoogleApiClient driveApiClient) {
		return new BackupFilePicker(activity, driveApiClient);
	}

	private BackupFilePicker(Activity activity, GoogleApiClient driveApiClient) {
		this.activity = activity;
		this.driveApiClient = driveApiClient;
	}

	public void startBackupFileCreation(Contents fileContents) {
		try {
			IntentSender intentSender = buildBackupFileCreationIntentSender(fileContents);
			activity.startIntentSenderForResult(intentSender, Intents.Requests.DRIVE_FILE_CREATE, null, 0, 0, 0);
		} catch (IntentSender.SendIntentException e) {
			throw new RuntimeException(e);
		}
	}

	private IntentSender buildBackupFileCreationIntentSender(Contents fileContents) {
		MetadataChangeSet fileMetadata = new MetadataChangeSet.Builder()
			.setTitle(buildBackupTitle())
			.setMimeType(BACKUP_MIME_TYPE)
			.build();

		return Drive.DriveApi.newCreateFileActivityBuilder()
			.setInitialMetadata(fileMetadata)
			.setInitialContents(fileContents)
			.build(driveApiClient);
	}

	private String buildBackupTitle() {
		return activity.getString(R.string.name_backup);
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
