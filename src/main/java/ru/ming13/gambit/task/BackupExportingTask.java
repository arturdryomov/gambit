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

package ru.ming13.gambit.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.drive.DriveId;

import ru.ming13.gambit.backup.BackupOperator;
import ru.ming13.gambit.bus.BackupFinishedEvent;
import ru.ming13.gambit.bus.BusEvent;
import ru.ming13.gambit.bus.BusProvider;

public class BackupExportingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final BackupOperator backupOperator;

	private final DriveId backupFileId;

	public static void execute(@NonNull BackupOperator backupOperator, @NonNull DriveId backupFileId) {
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
