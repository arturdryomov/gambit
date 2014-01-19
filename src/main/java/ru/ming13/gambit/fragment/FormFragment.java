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


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import ru.ming13.gambit.R;


abstract class FormFragment extends Fragment
{
	private static final boolean ENABLE_OPTIONS_MENU_FILLING = true;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return inflateFragment(layoutInflater, fragmentContainer);
	}

	protected abstract View inflateFragment(LayoutInflater layoutInflater, ViewGroup fragmentContainer);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(ENABLE_OPTIONS_MENU_FILLING);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_action_bar_form, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_done:
				checkAndAcceptUserData();
				return true;

			case R.id.menu_cancel:
				performCancelAction();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void checkAndAcceptUserData() {
		readUserDataFromFields();

		if (isUserDataCorrect()) {
			performAcceptAction();
		}
		else {
			setUpErrorMessages();
		}
	}

	protected abstract void readUserDataFromFields();

	protected abstract boolean isUserDataCorrect();

	protected abstract void performAcceptAction();

	protected abstract void setUpErrorMessages();

	protected abstract void performCancelAction();

	protected String getTextFromEdit(int editTextId) {
		return getEdit(editTextId).getText().toString().trim();
	}

	private EditText getEdit(int editTextId) {
		return (EditText) getView().findViewById(editTextId);
	}

	protected void setTextToEdit(int editTextId, String text) {
		getEdit(editTextId).setText(text);
	}

	protected void setErrorToEdit(int editTextId, int errorMessageResourceId) {
		getEdit(editTextId).setError(getString(errorMessageResourceId));
	}
}
