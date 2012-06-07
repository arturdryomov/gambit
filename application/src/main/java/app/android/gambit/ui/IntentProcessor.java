package app.android.gambit.ui;


import android.app.Activity;
import android.os.Bundle;


class IntentProcessor
{
	public static Object getMessage(Activity activity) {
		Bundle messageData = activity.getIntent().getExtras();

		Object message = messageData.getParcelable(IntentFactory.MESSAGE_ID);

		if (message == null) {
			throw new IntentCorruptedException();
		}

		return message;
	}
}
