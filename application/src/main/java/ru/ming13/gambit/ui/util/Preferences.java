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

package ru.ming13.gambit.ui.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public final class Preferences
{
	public static final class Keys
	{
		private Keys() {
		}

		public static final String EXAMPLE_DECK_CREATED = "example_deck_created";
	}

	private Preferences() {
	}

	public static void set(Context context, String key, boolean value) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(context);

		preferencesEditor.putBoolean(key, value);

		preferencesEditor.commit();
	}

	private static SharedPreferences.Editor getPreferencesEditor(Context context) {
		return getSharedPreferences(context).edit();
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	public static boolean getBoolean(Context context, String key) {
		return getSharedPreferences(context).getBoolean(key, false);
	}
}
