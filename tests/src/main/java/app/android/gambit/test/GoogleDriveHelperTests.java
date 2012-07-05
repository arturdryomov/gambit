package app.android.gambit.test;


import java.io.IOException;
import java.io.InputStream;

import android.accounts.Account;
import android.app.Activity;
import android.test.InstrumentationTestCase;
import app.android.gambit.InternetDateTime;
import app.android.gambit.remote.GoogleDriveHelper;
import app.android.gambit.ui.AccountSelector;
import app.android.gambit.ui.Authorizer;
import app.android.gambit.ui.DeckCreationActivity;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;
import com.google.api.services.drive.model.File;


public class GoogleDriveHelperTests extends InstrumentationTestCase
{
	private static String token;
	private static Activity hostActivity;
	public static final String MIME_GOOGLE_SPREADSHEET = "application/vnd.google-apps.spreadsheet";

	private GoogleDriveHelper driveHelper;
	private Drive driveService; // Needed to prepare some testing data

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ensureAuthorized();

		driveHelper = new GoogleDriveHelper(token, getApiKey());
		driveService = buildDriveService();
	}

	private void ensureAuthorized() {
		if (hostActivity == null) {
			hostActivity = launchActivity("app.android.gambit", DeckCreationActivity.class, null);
		}

		if (token == null) {
			Account account = AccountSelector.select(hostActivity);
			Authorizer authorizer = new Authorizer(hostActivity);
			token = authorizer.getToken(Authorizer.ServiceType.DRIVE, account);
			authorizer.invalidateToken(token);
			token = authorizer.getToken(Authorizer.ServiceType.DRIVE, account);
		}
	}

	private String getApiKey() {
		return hostActivity.getString(app.android.gambit.R.string.google_api_key);
	}

	private Drive buildDriveService() {
		HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		Drive.Builder driveServiceBuilder = new Drive.Builder(httpTransport, jsonFactory, null);
		driveServiceBuilder.setJsonHttpRequestInitializer(new JsonHttpRequestInitializer()
		{
			@Override
			public void initialize(JsonHttpRequest jsonHttpRequest) throws IOException {
				DriveRequest driveRequest = (DriveRequest) jsonHttpRequest;

				driveRequest.setOauthToken(token);
				driveRequest.setKey(getApiKey());
			}
		});

		return driveServiceBuilder.build();
	}

	@Override
	protected void tearDown() throws Exception {
		hostActivity.finish();
		super.tearDown();
	}

	public void testUploadXlsData() throws IOException {
		String key = createNewSpreadsheet();
		InputStream xlsDataInputStream = generateXlsData(key);

		// No exceptions is test pass criteria
		driveHelper.uploadXlsData(key, xlsDataInputStream);
	}

	private InputStream generateXlsData(String key) {
		// TODO: Implement a smarter way to generate XLS data with no need to use Google Drive
		return driveHelper.downloadXlsData(key);
	}

	public void testCreateSpreadsheetFromXlsData() throws IOException {
		String key = createNewSpreadsheet();
		InputStream xlsDataInputStream = generateXlsData(key);

		// No exceptions is test pass criteria
		driveHelper.createSpreadsheetFromXlsData("New spreadsheet", xlsDataInputStream);
	}

	public void testDownloadXlsData() throws IOException {
		// TODO: Check whether obtain xls data is valid.
		// Currently just make sure it returns something without throwing.
		String key = createNewSpreadsheet();
		InputStream xlsDataInputStream = driveHelper.downloadXlsData(key);
	}

	private String createNewSpreadsheet() throws IOException {
		return createNewSpreadsheet("Test file");
	}

	private String createNewSpreadsheet(String spreadsheetName) throws IOException {
		File file = new File();
		file.setTitle(spreadsheetName);
		file.setMimeType(MIME_GOOGLE_SPREADSHEET);

		return driveService.files().insert(file).execute().getId();
	}

	public void testGetNewestSpreadsheetKeyByName() throws IOException {
		final String SPREADSHEET_NAME = "Test spreadsheet";

		String realKey = createNewSpreadsheet(SPREADSHEET_NAME);
		String obtainedKey = driveHelper.getNewestSpreadsheetKeyByName(SPREADSHEET_NAME);

		assertEquals(realKey, obtainedKey);
	}

	public void testGetSpreadsheetUpdateTime() throws IOException, InterruptedException {
		// We need time delta because we're going to compare local date with
		// date on Google servers. There obviously might be some difference.
		final int TIME_DELTA_IN_MILLIS = 60 * 1000;

		InternetDateTime beforeCreation = new InternetDateTime();
		Thread.sleep(TIME_DELTA_IN_MILLIS);

		String key = createNewSpreadsheet();
		InternetDateTime dateTime = driveHelper.getSpreadsheetUpdateTime(key);
		Thread.sleep(TIME_DELTA_IN_MILLIS);

		InternetDateTime afterCreation = new InternetDateTime();

		assertTrue(beforeCreation.isBefore(dateTime));
		assertTrue(afterCreation.isAfter(dateTime));
	}
}
