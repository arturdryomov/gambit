package app.android.simpleflashcards.ui;


import android.app.ProgressDialog;
import android.content.Context;


public class ProgressDialogShowHelper
{
	private ProgressDialog progressDialog;

	public void show(Context context, String text) {
		progressDialog = ProgressDialog.show(context, new String(), text);
	}

	public void hide() {
		progressDialog.dismiss();
	}
}