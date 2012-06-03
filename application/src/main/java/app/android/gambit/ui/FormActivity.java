package app.android.gambit.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import app.android.gambit.R;
import com.actionbarsherlock.app.SherlockActivity;


abstract class FormActivity extends SherlockActivity
{
	protected final Context activityContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeConfirmButton();
	}

	protected void initializeConfirmButton() {
		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setOnClickListener(confirmListener);
	}

	private final View.OnClickListener confirmListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view) {
			readUserDataFromFields();

			String userDataErrorMessage = getUserDataErrorMessage();

			if (userDataErrorMessage.isEmpty()) {
				performSubmitAction();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}
	};

	protected abstract void readUserDataFromFields();

	protected abstract String getUserDataErrorMessage();

	protected abstract void performSubmitAction();
}
