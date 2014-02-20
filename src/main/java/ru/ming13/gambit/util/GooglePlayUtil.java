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

package ru.ming13.gambit.util;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public final class GooglePlayUtil
{
	private final Activity activity;

	public static GooglePlayUtil with(Activity activity) {
		return new GooglePlayUtil(activity);
	}

	private GooglePlayUtil(Activity activity) {
		this.activity = activity;
	}

	public void resolve(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			showResolutionAction(connectionResult);
		} else {
			showResolutionError(connectionResult);
		}
	}

	private void showResolutionAction(ConnectionResult connectionResult) {
		try {
			connectionResult.startResolutionForResult(activity, Intents.Requests.GOOGLE_CONNECTION);
		} catch (IntentSender.SendIntentException e) {
			throw new RuntimeException(e);
		}
	}

	private void showResolutionError(ConnectionResult connectionResult) {
		GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), activity, Intents.Requests.GOOGLE_CONNECTION);
	}
}
