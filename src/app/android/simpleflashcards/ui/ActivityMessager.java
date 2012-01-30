package app.android.simpleflashcards.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class ActivityMessager
{
	private static final String MESSAGE_ID = "message";
	
	public static void startActivityWithMessage(Context context, Class<?> activityClass, int message) {
		Intent callIntent = new Intent(context, activityClass);

		callIntent.putExtra(MESSAGE_ID, message);
		
		context.startActivity(callIntent);
	}
	
	public static int getMessageFromActivity(Activity receiverActivity) {
		int message = 0;

		Intent receivedIntent = receiverActivity.getIntent();
		if (receivedIntent.hasExtra(MESSAGE_ID)) {
			message = receivedIntent.getIntExtra(MESSAGE_ID, 0);
		}
		
		return message;
	}
}
