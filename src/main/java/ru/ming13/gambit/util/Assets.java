/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
