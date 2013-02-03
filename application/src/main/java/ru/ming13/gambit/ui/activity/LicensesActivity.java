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


import android.content.ContentResolver;
import android.os.Bundle;
import android.webkit.WebView;
import com.actionbarsherlock.app.SherlockActivity;
import ru.ming13.gambit.R;


public class LicensesActivity extends SherlockActivity
{
	private static final String SCHEME = ContentResolver.SCHEME_FILE;
	private static final String AUTHORITY = "android_asset";
	private static final String PATH = "licenses.html";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_licenses);

		setUpLicenses();

		restoreWebViewState(savedInstanceState);
	}

	private void setUpLicenses() {
		getWebView().loadUrl(buildLicensesUri());
	}

	private WebView getWebView() {
		return (WebView) findViewById(R.id.licenses_webview);
	}

	private String buildLicensesUri() {
		return String.format("%s:///%s/%s", SCHEME, AUTHORITY, PATH);
	}

	private void restoreWebViewState(Bundle savedState) {
		if (savedState != null) {
			getWebView().restoreState(savedState);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		getWebView().saveState(outState);

		super.onSaveInstanceState(outState);
	}
}
