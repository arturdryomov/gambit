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
