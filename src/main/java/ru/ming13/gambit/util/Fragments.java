package ru.ming13.gambit.util;

import android.app.Activity;
import android.app.Fragment;

public final class Fragments
{
	private Fragments() {
	}

	public static final class Arguments
	{
		private Arguments() {
		}

		public static final String CARD = "card";
		public static final String URI = "uri";
	}

	public static final class Operator
	{
		private Operator() {
		}

		public static void set(Activity activity, Fragment fragment) {
			if (isSet(activity)) {
				return;
			}

			activity.getFragmentManager()
				.beginTransaction()
				.add(android.R.id.content, fragment)
				.commit();
		}

		private static boolean isSet(Activity activity) {
			return activity.getFragmentManager().findFragmentById(android.R.id.content) != null;
		}
	}
}
