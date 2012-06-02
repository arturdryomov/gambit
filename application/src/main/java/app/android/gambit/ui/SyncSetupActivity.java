package app.android.gambit.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import app.android.gambit.R;


public class SyncSetupActivity extends Activity
{
	private final Context activityContext = this;

	private static enum SyncMode {
		EXISTING, NEW
	}

	private SyncMode currentSyncMode;

	private final List<HashMap<String, Object>> spreadsheets;

	private static final String SPREADSHEET_ITEM_TEXT_ID = "text";
	private static final String SPREADSHEET_ITEM_OBJECT_ID = "object";

	public SyncSetupActivity() {
		super();

		spreadsheets = new ArrayList<HashMap<String, Object>>();

		currentSyncMode = SyncMode.NEW;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitiy_sync_setup);

		initializeBodyControls();

		new LoadSpreadsheetsTask().execute();
	}

	private void initializeBodyControls() {
		EditText spreadsheetNameEdit = (EditText) findViewById(R.id.edit_spreadsheet_name);
		spreadsheetNameEdit.setText(getString(R.string.cards));

		initializeSpreadsheetsAdapter();

		CheckBox syncModeCheckbox = (CheckBox) findViewById(R.id.checkbox_sync_with_existing_spreadsheet);
		syncModeCheckbox.setChecked(false);
		syncModeCheckbox.setOnCheckedChangeListener(syncModeListener);
	}

	private void initializeSpreadsheetsAdapter() {
		Spinner spreadsheetsSpinner = (Spinner) findViewById(R.id.spinner_spreadsheets);

		SimpleAdapter spreadsheetsAdapter = new SimpleAdapter(activityContext, spreadsheets,
			R.layout.list_item_two_line, new String[] { SPREADSHEET_ITEM_TEXT_ID },
			new int[] { android.R.layout.simple_spinner_item });

		spreadsheetsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spreadsheetsSpinner.setAdapter(spreadsheetsAdapter);
	}

	private final OnCheckedChangeListener syncModeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				currentSyncMode = SyncMode.EXISTING;
			}
			else {
				currentSyncMode = SyncMode.NEW;
			}

			updateSyncModeDependentControls();
		}
	};

	private void updateSyncModeDependentControls() {
		EditText spreadsheetNameEdit = (EditText) findViewById(R.id.edit_spreadsheet_name);
		Spinner spreadsheetsSpinner = (Spinner) findViewById(R.id.spinner_spreadsheets);

		switch (currentSyncMode) {
			case NEW:
				spreadsheetNameEdit.setVisibility(View.VISIBLE);
				spreadsheetsSpinner.setVisibility(View.GONE);
				break;

			case EXISTING:
				spreadsheetNameEdit.setVisibility(View.GONE);
				spreadsheetsSpinner.setVisibility(View.VISIBLE);
				break;

			default:
				break;
		}
	}

	private class LoadSpreadsheetsTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.loadingSpreadsheets));
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO: Load spreadsheets and fill spinner

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			progressDialogHelper.hide();

			if (errorMessage.isEmpty()) {
				if (spreadsheets.isEmpty()) {
					hideSyncModeCheckbox();
				}
				else {
					updateSpreadsheetsSpinner();
				}
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}

		private void hideSyncModeCheckbox() {
			CheckBox syncModeCheckbox = (CheckBox) findViewById(R.id.checkbox_sync_with_existing_spreadsheet);
			syncModeCheckbox.setVisibility(View.GONE);
		}

		private void updateSpreadsheetsSpinner() {
			Spinner spreadsheetsSpinenr = (Spinner) findViewById(R.id.spinner_spreadsheets);
			((SimpleAdapter) spreadsheetsSpinenr.getAdapter()).notifyDataSetChanged();
		}
	}
}
