package app.android.gambit.test;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.accounts.Account;
import android.app.Activity;
import android.test.InstrumentationTestCase;
import app.android.gambit.InternetDateTime;
import app.android.gambit.remote.GoogleDriveHelper;
import app.android.gambit.remote.RemoteCard;
import app.android.gambit.remote.RemoteDeck;
import app.android.gambit.remote.RemoteDecksConverter;
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
	private static String authToken;
	private static Activity hostActivity;
	public static final String MIME_GOOGLE_SPREADSHEET = "application/vnd.google-apps.spreadsheet";

	private GoogleDriveHelper driveHelper;
	private Drive driveService; // Needed to prepare some testing data

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ensureAuthorized();

		driveHelper = new GoogleDriveHelper(authToken, getApiKey());
		driveService = buildDriveService();
	}

	private void ensureAuthorized() {
		if (hostActivity == null) {
			hostActivity = launchActivity("app.android.gambit", DeckCreationActivity.class, null);
		}

		if (authToken == null) {
			Account account = AccountSelector.select(hostActivity);
			Authorizer authorizer = new Authorizer(hostActivity);
			authToken = authorizer.getToken(Authorizer.ServiceType.DRIVE, account);
			authorizer.invalidateToken(authToken);
			authToken = authorizer.getToken(Authorizer.ServiceType.DRIVE, account);
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

				driveRequest.setOauthToken(authToken);
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

	public void testUpdateSpreadsheet() throws IOException {
		byte[] xlsData = generateXlsData();
		String spreadsheetKey = createSpreadsheet();

		// No exceptions is test pass criteria
		driveHelper.updateSpreadsheet(spreadsheetKey, xlsData);
	}

	private byte[] generateXlsData() {
		RemoteDecksConverter remoteDecksConverter = new RemoteDecksConverter();
		return remoteDecksConverter.toXlsData(generateRemoteDecks());
	}

	public List<RemoteDeck> generateRemoteDecks() {
		final int DECKS_COUNT = 1;
		final int CARDS_COUNT = 1;

		List<RemoteDeck> remoteDecks = new ArrayList<RemoteDeck>();

		for (int deckIndex = 0; deckIndex < DECKS_COUNT; deckIndex++) {

			List<RemoteCard> remoteCards = new ArrayList<RemoteCard>();
			for (int cardIndex = 0; cardIndex < CARDS_COUNT; cardIndex++) {
				remoteCards.add(new RemoteCard(String.format("Front %s", cardIndex + 1),
					String.format("Back %s", cardIndex + 1)));
			}

			remoteDecks.add(new RemoteDeck(String.format("Deck %s", deckIndex + 1), remoteCards));
		}

		return remoteDecks;
	}

	public void testCreateSpreadsheet() throws IOException {
		byte[] xlsData = generateXlsData();

		// No exceptions is test pass criteria
		driveHelper.createSpreadsheet("New spreadsheet", xlsData);
	}

	public void testDownloadXlsData() throws IOException {
		String spreadsheetKey = createSpreadsheet();

		InputStream xlsDataInputStream = driveHelper.downloadXlsData(spreadsheetKey);

		ensureXlsDataCorrect(xlsDataInputStream);
	}

	private String createSpreadsheet() throws IOException {
		return createSpreadsheet("Test file");
	}

	private String createSpreadsheet(String spreadsheetName) throws IOException {
		File spreadsheetFile = new File();
		spreadsheetFile.setTitle(spreadsheetName);
		spreadsheetFile.setMimeType(MIME_GOOGLE_SPREADSHEET);

		return driveService.files().insert(spreadsheetFile).execute().getId();
	}

	private void ensureXlsDataCorrect(InputStream xlsData) {
		RemoteDecksConverter remoteDecksConverter = new RemoteDecksConverter();

		// This will throw if xls data is invalid
		@SuppressWarnings("unused")
		List<RemoteDeck> decks = remoteDecksConverter.fromXlsData(xlsData);
	}

	public void testGetNewestSpreadsheetKey() throws IOException {
		final String SPREADSHEET_NAME = "Test spreadsheet";

		String expectedSpreadsheetKey = createSpreadsheet(SPREADSHEET_NAME);
		String obtainedSpreadsheetKey = driveHelper.getNewestSpreadsheetKey(SPREADSHEET_NAME);

		assertEquals(expectedSpreadsheetKey, obtainedSpreadsheetKey);
	}

	public void testGetSpreadsheetUpdateTime() throws IOException, InterruptedException {
		// We need time delta because we're going to compare local date with
		// date on Google servers. There obviously might be some difference.
		final int TIME_DELTA_IN_SECONDS = 60;

		InternetDateTime timeBeforeCreation = addSecondsToDateTime(new InternetDateTime(),
			-TIME_DELTA_IN_SECONDS);

		String spreadsheetKey = createSpreadsheet();
		InternetDateTime spreadsheetModifiedTime = driveHelper.getSpreadsheetUpdateTime(spreadsheetKey);

		InternetDateTime timeAfterCreation = addSecondsToDateTime(new InternetDateTime(),
			TIME_DELTA_IN_SECONDS);

		// This is not a strict check, but it will at least make sure driveHelper
		// doesn't return nonsense
		assertTrue(timeBeforeCreation.isBefore(spreadsheetModifiedTime));
		assertTrue(timeAfterCreation.isAfter(spreadsheetModifiedTime));
	}

	private InternetDateTime addSecondsToDateTime(InternetDateTime dateTime, int seconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(dateTime.toDate());

		calendar.add(Calendar.SECOND, seconds);

		return new InternetDateTime(calendar.getTime());
	}
}
