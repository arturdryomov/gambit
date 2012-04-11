package app.android.gambit;


import android.app.Application;
import app.android.gambit.models.DatabaseProvider;


public class GambitApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		DatabaseProvider.getInstance(this);
	}
}
