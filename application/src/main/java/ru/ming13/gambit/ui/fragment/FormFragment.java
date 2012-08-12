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

package ru.ming13.gambit.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import ru.ming13.gambit.R;


/**
 * Parent activity should implement FormCallback to handle cancel and accept.
 * You could call finish() in callback methods for example.
 */
abstract class FormFragment extends SherlockFragment
{
	private static final boolean ENABLE_OPTIONS_MENU_FILLING = true;

	public static interface FormCallback
	{
		public <Data> void onAccept(Data data);

		public void onCancel();
	}

	private FormCallback formCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof FormCallback)) {
			throw new FragmentException();
		}

		formCallback = (FormCallback) activity;
	}

	protected FormCallback getFormCallback() {
		return formCallback;
	}

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
			case R.id.menu_accept:
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
			performAcceptAction(null);
		}
		else {
			setUpErrorMessages();
		}
	}

	protected abstract void readUserDataFromFields();

	protected abstract boolean isUserDataCorrect();

	/**
	 * If this method calls some async code you should call super.performAcceptAction()
	 * on its’ finish to provide callback for the parent activity and finish job correct way.
	 */
	protected <Data> void performAcceptAction(Data data) {
		formCallback.onAccept(data);
	}

	protected abstract void setUpErrorMessages();

	protected void performCancelAction() {
		formCallback.onCancel();
	}

	protected String getTextFromEdit(int editTextId) {
		return getEdit(editTextId).getText().toString().trim();
	}

	private EditText getEdit(int editTextId) {
		return (EditText) getView().findViewById(editTextId);
	}

	protected void setTextToEdit(int editTextId, String text) {
		getEdit(editTextId).setText(text);
	}

	protected void setErrorToEdit(int editTextId, String errorMessage) {
		getEdit(editTextId).setError(errorMessage);
	}
}
