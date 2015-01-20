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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public final class Fragments
{
	private Fragments() {
	}

	public static final class Arguments
	{
		private Arguments() {
		}

		public static final String DECK = "deck";
		public static final String CARD = "card";

		public static final String MESSAGE = "message";

		public static final String ERROR_CODE = "error_code";
		public static final String REQUEST_CODE = "request_code";
	}

	public static final class Operator
	{
		private final FragmentManager fragmentManager;

		public static Operator at(@NonNull Activity activity) {
			return new Operator(activity);
		}

		private Operator(Activity activity) {
			this.fragmentManager = activity.getFragmentManager();
		}

		public void reset(@NonNull Fragment fragment, @IdRes int fragmentContainerId) {
			fragmentManager
				.beginTransaction()
				.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
				.replace(fragmentContainerId, fragment)
				.commit();
		}

		public void set(@NonNull Fragment fragment, @IdRes int fragmentContainerId) {
			if (!isSet(fragmentContainerId)) {
				fragmentManager
					.beginTransaction()
					.add(fragmentContainerId, fragment)
					.commit();
			}
		}

		private boolean isSet(@IdRes int fragmentContainerId) {
			return get(fragmentContainerId) != null;
		}

		private Fragment get(@IdRes int fragmentContainerId) {
			return fragmentManager.findFragmentById(fragmentContainerId);
		}

		public void remove(@IdRes int fragmentContainerId) {
			if (isSet(fragmentContainerId)) {
				fragmentManager
					.beginTransaction()
					.remove(get(fragmentContainerId))
					.commit();
			}
		}
	}
}
