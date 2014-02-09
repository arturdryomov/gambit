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

package ru.ming13.gambit.fragment;

import android.os.Bundle;
import android.webkit.WebViewFragment;

import ru.ming13.gambit.util.Assets;

public class LicensesFragment extends WebViewFragment
{
	public static LicensesFragment newInstance() {
		return new LicensesFragment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpLicenses();
	}

	private void setUpLicenses() {
		getWebView().loadUrl(Assets.getLicensesUri());
	}
}
