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

package ru.ming13.gambit.ui;


import android.app.ProgressDialog;
import android.content.Context;


class ProgressDialogHelper
{
	private ProgressDialog progressDialog = null;

	public void show(Context context, String text) {
		progressDialog = ProgressDialog.show(context, new String(), text);
	}

	public void show(Context context, int textResourceId) {
		show(context, context.getString(textResourceId));
	}

	public void hide() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}