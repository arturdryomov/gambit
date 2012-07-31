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

package app.android.gambit.ui;


import android.text.TextUtils;
import android.widget.EditText;
import app.android.gambit.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


abstract class FormActivity extends SherlockActivity
{
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_item_editing, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_accept:
				checkAndAcceptUserData();
				return true;

			case R.id.menu_cancel:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void checkAndAcceptUserData() {
		readUserDataFromFields();

		String userDataErrorMessage = getUserDataErrorMessage();

		if (TextUtils.isEmpty(userDataErrorMessage)) {
			performSubmitAction();
		}
		else {
			UserAlerter.alert(this, userDataErrorMessage);
		}
	}

	protected abstract void readUserDataFromFields();

	protected String getTextFromEdit(int editTextId) {
		EditText editText = (EditText) findViewById(editTextId);

		return editText.getText().toString().trim();
	}

	protected abstract String getUserDataErrorMessage();

	protected abstract void performSubmitAction();
}
