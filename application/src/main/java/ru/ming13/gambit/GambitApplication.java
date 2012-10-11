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

package ru.ming13.gambit;


import android.app.Application;
import com.bugsense.trace.BugSenseHandler;
import ru.ming13.gambit.local.DbProvider;


public class GambitApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpDatabase();
		setUpBugsense();
	}

	private void setUpDatabase() {
		DbProvider.getInstance(this);
	}

	private void setUpBugsense() {
		if (isBugsenseEnabled()) {
			BugSenseHandler.initAndStartSession(this, getBugsenseProjectKey());
		}
	}

	private boolean isBugsenseEnabled() {
		return getResources().getBoolean(R.bool.flag_bugsense_enabled);
	}

	private String getBugsenseProjectKey() {
		return getString(R.string.key_bugsense_project);
	}
}
