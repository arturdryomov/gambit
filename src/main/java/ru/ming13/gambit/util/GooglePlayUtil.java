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
