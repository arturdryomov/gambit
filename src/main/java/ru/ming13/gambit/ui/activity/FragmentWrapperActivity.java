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

package ru.ming13.gambit.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragmentActivity;


abstract class FragmentWrapperActivity extends SherlockFragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpFragment();
	}

	private void setUpFragment() {
		if (!isFragmentInstalled()) {
			installFragment();
		}
	}

	private boolean isFragmentInstalled() {
		return getSupportFragmentManager().findFragmentById(android.R.id.content) != null;
	}

	private void installFragment() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.add(android.R.id.content, buildFragment());

		fragmentTransaction.commit();
	}

	protected abstract Fragment buildFragment();
}