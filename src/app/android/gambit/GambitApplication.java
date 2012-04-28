package app.android.gambit;


import android.app.Application;
import app.android.gambit.local.DbProvider;


public class GambitApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		DbProvider.getInstance(this);
	}
}
