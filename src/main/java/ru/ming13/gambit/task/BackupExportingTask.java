package ru.ming13.gambit.task;

import android.os.AsyncTask;

import com.google.android.gms.drive.DriveId;

import ru.ming13.gambit.backup.BackupOperator;
import ru.ming13.gambit.bus.BackupFinishedEvent;
import ru.ming13.gambit.bus.BusEvent;
import ru.ming13.gambit.bus.BusProvider;

public class BackupExportingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final BackupOperator backupOperator;
	private final DriveId backupFileId;

	public static void execute(BackupOperator backupOperator, DriveId backupFileId) {
		new BackupExportingTask(backupOperator, backupFileId).execute();
	}

	private BackupExportingTask(BackupOperator backupOperator, DriveId backupFileId) {
		this.backupOperator = backupOperator;
		this.backupFileId = backupFileId;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		backupOperator.exportBackup(backupFileId);

		return new BackupFinishedEvent();
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
