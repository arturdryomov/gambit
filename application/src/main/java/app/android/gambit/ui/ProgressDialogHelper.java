package app.android.gambit.ui;


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
