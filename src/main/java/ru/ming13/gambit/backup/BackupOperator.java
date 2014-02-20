package ru.ming13.gambit.backup;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.InputStream;
import java.io.OutputStream;

import ru.ming13.gambit.database.DatabaseOperator;

public final class BackupOperator
{
	public static final String BACKUP_MIME_TYPE = "application/x-sqlite3";

	private final Context context;
	private final GoogleApiClient driveApiClient;

	public static BackupOperator with(Context context, GoogleApiClient driveApiClient) {
		return new BackupOperator(context, driveApiClient);
	}

	private BackupOperator(Context context, GoogleApiClient driveApiClient) {
		this.context = context.getApplicationContext();
		this.driveApiClient = driveApiClient;
	}

	public void exportBackup(DriveId backupFileId) {
		DriveFile backupFile = Drive.DriveApi.getFile(driveApiClient, backupFileId);
		Contents backupFileContents = backupFile.openContents(driveApiClient, DriveFile.MODE_WRITE_ONLY, null).await().getContents();

		OutputStream backupFileStream = backupFileContents.getOutputStream();
		DatabaseOperator.with(context).writeDatabaseContents(backupFileStream);

		backupFile.commitAndCloseContents(driveApiClient, backupFileContents).await();
	}

	public void importBackup(DriveId backupFileId) {
		DriveFile backupFile = Drive.DriveApi.getFile(driveApiClient, backupFileId);
		Contents backupFileContents = backupFile.openContents(driveApiClient, DriveFile.MODE_READ_ONLY, null).await().getContents();

		InputStream backupFileStream = backupFileContents.getInputStream();
		DatabaseOperator.with(context).readDatabaseContents(backupFileStream);

		backupFile.discardContents(driveApiClient, backupFileContents).await();
	}
}
