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

package ru.ming13.gambit.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesUtil;

import ru.ming13.gambit.util.Fragments;

public class GoogleServicesErrorDialog extends DialogFragment
{
	public static final String TAG = "google_services_error_dialog";

	public static GoogleServicesErrorDialog newInstance(int errorCode, int requestCode) {
		GoogleServicesErrorDialog dialog = new GoogleServicesErrorDialog();

		dialog.setArguments(buildArguments(errorCode, requestCode));

		return dialog;
	}

	private static Bundle buildArguments(int errorCode, int requestCode) {
		Bundle arguments = new Bundle();

		arguments.putInt(Fragments.Arguments.ERROR_CODE, errorCode);
		arguments.putInt(Fragments.Arguments.REQUEST_CODE, requestCode);

		return arguments;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return GooglePlayServicesUtil.getErrorDialog(getErrorCode(), getActivity(), getRequestCode());
	}

	private int getErrorCode() {
		return getArguments().getInt(Fragments.Arguments.ERROR_CODE);
	}

	private int getRequestCode() {
		return getArguments().getInt(Fragments.Arguments.REQUEST_CODE);
	}
}
