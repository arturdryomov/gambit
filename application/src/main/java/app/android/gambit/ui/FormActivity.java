package app.android.gambit.ui;


import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import app.android.gambit.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


abstract class FormActivity extends SherlockActivity
{
	protected final Context activityContext = this;

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
			UserAlerter.alert(activityContext, userDataErrorMessage);
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
