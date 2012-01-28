package app.android.simpleflashcards.ui;


import android.content.Context;
import android.content.Intent;


public class ActivityStarter
{
	public static void start(Context context, Class<?> activityClass) {
		Intent callIntent = new Intent(context, activityClass);
		context.startActivity(callIntent);
	}
}