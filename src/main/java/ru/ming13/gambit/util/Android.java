package ru.ming13.gambit.util;

import android.content.Context;
import android.content.res.Configuration;

import ru.ming13.gambit.R;

public final class Android
{
	private final Context context;

	public static Android with(Context context) {
		return new Android(context);
	}

	private Android(Context context) {
		this.context = context;
	}

	public boolean isTablet() {
		return context.getResources().getBoolean(R.bool.tablet);
	}

	public boolean isLandscape() {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
}
