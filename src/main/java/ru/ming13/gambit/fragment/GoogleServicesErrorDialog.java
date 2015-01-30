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

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.google.android.gms.common.GooglePlayServicesUtil;

import butterknife.ButterKnife;
import ru.ming13.gambit.util.Fragments;

public class GoogleServicesErrorDialog extends DialogFragment
{
	public static final String TAG = "google_services_error_dialog";

	@InjectExtra(Fragments.Arguments.ERROR_CODE)
	int errorCode;

	@InjectExtra(Fragments.Arguments.REQUEST_CODE)
	int requestCode;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setUpInjections();

		return GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), requestCode);
	}

	private void setUpInjections() {
		Dart.inject(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		tearDownInjections();
	}

	private void tearDownInjections() {
		ButterKnife.reset(this);
	}
}
