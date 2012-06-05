package app.android.gambit.ui;


import android.content.Context;
import android.widget.Toast;


class UserAlerter
{
	public static void alert(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void alert(Context context, int textResourceId) {
		alert(context, context.getString(textResourceId));
	}
}