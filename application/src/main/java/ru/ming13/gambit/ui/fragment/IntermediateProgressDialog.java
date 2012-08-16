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

package ru.ming13.gambit.ui.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;


public class IntermediateProgressDialog extends SherlockDialogFragment
{
	public static final String TAG = "progress_dialog";

	private static boolean CANCELABLE = false;

	public static IntermediateProgressDialog newInstance(String message) {
		IntermediateProgressDialog progressDialog = new IntermediateProgressDialog();

		progressDialog.setArguments(buildArguments(message));

		progressDialog.setCancelable(CANCELABLE);

		return progressDialog;
	}

	private static Bundle buildArguments(String message) {
		Bundle arguments = new Bundle();

		arguments.putString(FragmentArguments.MESSAGE, message);

		return arguments;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return buildProgressDialog();
	}

	private ProgressDialog buildProgressDialog() {
		ProgressDialog progressDialog = new ProgressDialog(getActivity());

		progressDialog.setMessage(getMessage());

		return progressDialog;
	}

	private String getMessage() {
		return getArguments().getString(FragmentArguments.MESSAGE);
	}
}
