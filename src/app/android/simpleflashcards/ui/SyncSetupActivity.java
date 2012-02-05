package app.android.simpleflashcards.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import app.android.simpleflashcards.R;


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
		setContentView(R.layout.sync_setup);

		initializeBodyControls();
	}

	private void initializeBodyControls() {
		EditText spreadsheetNameEdit = (EditText) findViewById(R.id.spreadsheetNameEdit);
		spreadsheetNameEdit.setText(getString(R.string.cards));

		// TODO: Call spreadsheets loading task
		initializeSpreadsheetsAdapter();

		CheckBox syncModeCheckbox = (CheckBox) findViewById(R.id.syncWithExistingCheckbox);
		syncModeCheckbox.setChecked(false);
		syncModeCheckbox.setOnCheckedChangeListener(syncModeListener);
	}

	private void initializeSpreadsheetsAdapter() {
		Spinner spreadsheetsSpinner = (Spinner) findViewById(R.id.spreadsheetsSpinner);

		SimpleAdapter spreadsheetsAdapter = new SimpleAdapter(activityContext, spreadsheets,
			R.layout.cards_list_item, new String[] { SPREADSHEET_ITEM_TEXT_ID },
			new int[] { android.R.layout.simple_spinner_item });

		spreadsheetsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spreadsheetsSpinner.setAdapter(spreadsheetsAdapter);
	}

	private final OnCheckedChangeListener syncModeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				if (spreadsheets.isEmpty()) {
					UserAlerter.alert(activityContext, "There are no spreadsheets in your Google account");

					CheckBox checkbox = (CheckBox) buttonView;
					checkbox.setChecked(false);
				}
				else {
					currentSyncMode = SyncMode.EXISTING;
				}
			}
			else {
				currentSyncMode = SyncMode.NEW;
			}

			updateSyncModeDependentControls();
		}
	};

	private void updateSyncModeDependentControls() {
		EditText spreadsheetNameEdit = (EditText) findViewById(R.id.spreadsheetNameEdit);
		Spinner spreadsheetsSpinner = (Spinner) findViewById(R.id.spreadsheetsSpinner);

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
}
