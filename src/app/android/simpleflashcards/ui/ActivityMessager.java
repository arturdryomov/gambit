package app.android.simpleflashcards.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class ActivityMessager
{
	private static final String MESSAGE_ID = "message";
	private static final int DEFAULT_MESSAGE_VALUE = 0;

	public static void startActivityWithMessage(Context context, Class<?> activityClass, int message) {
		Intent callIntent = new Intent(context, activityClass);

		callIntent.putExtra(MESSAGE_ID, message);

		context.startActivity(callIntent);
	}

	public static int getMessageFromActivity(Activity receiverActivity) {
		Intent receivedIntent = receiverActivity.getIntent();

		return receivedIntent.getIntExtra(MESSAGE_ID, DEFAULT_MESSAGE_VALUE);
	}
}
