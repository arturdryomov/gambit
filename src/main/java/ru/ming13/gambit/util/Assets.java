package ru.ming13.gambit.util;

import android.content.ContentResolver;

public final class Assets
{
	private static final class Files
	{
		private Files() {
		}

		public static final String LICENSES = "licenses.html";
	}

	private static final class Uri
	{
		private Uri() {
		}

		public static final String MASK = "%s:///%s/%s";

		public static final String SCHEME = ContentResolver.SCHEME_FILE;
		public static final String AUTHORITY = "android_asset";
	}

	private Assets() {
	}

	public static String getLicensesUri() {
		return String.format(Uri.MASK, Uri.SCHEME, Uri.AUTHORITY, Files.LICENSES);
	}
}
