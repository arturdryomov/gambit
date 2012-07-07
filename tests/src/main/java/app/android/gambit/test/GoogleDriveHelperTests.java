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
import app.android.gambit.remote.ConvertingException;
import app.android.gambit.remote.FileNotExistsException;
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
	private static final String TESTING_SPREADSHEET_NAME = "Spreadsheet for Gambit testing purposes";

	private static final String MIME_GOOGLE_SPREADSHEET = "application/vnd.google-apps.spreadsheet";

	private static String authToken;
	private static Activity hostActivity;

	private GoogleDriveHelper driveHelper;
	private Drive driveService;

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
		removeTestingSpreadsheets();
		hostActivity.finish();

		super.tearDown();
	}

	private void removeTestingSpreadsheets() {
		while (true) {
			try {
				removeSpreadsheet(driveHelper.getNewestSpreadsheetKey(TESTING_SPREADSHEET_NAME));
			}
			catch (FileNotExistsException e) {
				return;
			}
		}
	}

	private void removeSpreadsheet(String spreadsheetKey) {
		try {
			driveService.files().delete(spreadsheetKey).execute();
		}
		catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void testCreateSpreadsheet() {
		byte[] xlsData = generateXlsData();

		try {
			driveHelper.createSpreadsheet(TESTING_SPREADSHEET_NAME, xlsData);
		}
		catch (RuntimeException e) {
			fail();
		}
	}

	private byte[] generateXlsData() {
		RemoteDecksConverter remoteDecksConverter = new RemoteDecksConverter();

		return remoteDecksConverter.toXlsData(generateRemoteDecks());
	}

	private List<RemoteDeck> generateRemoteDecks() {
		final int DECKS_COUNT = 1;
		final int CARDS_COUNT = 1;

		List<RemoteDeck> remoteDecks = new ArrayList<RemoteDeck>();

		for (int deckIndex = 0; deckIndex < DECKS_COUNT; deckIndex++) {
			List<RemoteCard> remoteCards = new ArrayList<RemoteCard>();

			for (int cardIndex = 0; cardIndex < CARDS_COUNT; cardIndex++) {
				remoteCards.add(generateRemoteCard(cardIndex));
			}

			remoteDecks.add(generateRemoteDeck(deckIndex, remoteCards));
		}

		return remoteDecks;
	}

	private RemoteCard generateRemoteCard(int cardIndex) {
		RemoteCard remoteCard = new RemoteCard();

		remoteCard.setFrontSideText(String.format("Front side #%d", cardIndex));
		remoteCard.setBackSideText(String.format("Back side #%d", cardIndex));

		return remoteCard;
	}

	private RemoteDeck generateRemoteDeck(int deckIndex, List<RemoteCard> remoteCards) {
		RemoteDeck remoteDeck = new RemoteDeck();

		remoteDeck.setTitle(String.format("Deck #%d", deckIndex + 1));
		remoteDeck.setCards(remoteCards);

		return remoteDeck;
	}

	public void testUpdateSpreadsheet() {
		byte[] xlsData = generateXlsData();
		String spreadsheetKey = createSpreadsheet(TESTING_SPREADSHEET_NAME);

		try {
			driveHelper.updateSpreadsheet(spreadsheetKey, xlsData);
		}
		catch (RuntimeException e) {
			fail();
		}
	}

	private String createSpreadsheet(String spreadsheetName) {
		File spreadsheetFile = new File();
		spreadsheetFile.setTitle(spreadsheetName);
		spreadsheetFile.setMimeType(MIME_GOOGLE_SPREADSHEET);

		try {
			return driveService.files().insert(spreadsheetFile).execute().getId();
		}
		catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void testDownloadXlsData() {
		String spreadsheetKey = createSpreadsheet(TESTING_SPREADSHEET_NAME);

		InputStream xlsData = driveHelper.downloadXlsData(spreadsheetKey);

		assertTrue(isXlsDataCorrect(xlsData));
	}

	private boolean isXlsDataCorrect(InputStream xlsData) {
		RemoteDecksConverter remoteDecksConverter = new RemoteDecksConverter();

		try {
			remoteDecksConverter.fromXlsData(xlsData);
		}
		catch (ConvertingException e) {
			return false;
		}

		return true;
	}

	public void testGetNewestSpreadsheetKey() {
		String expectedSpreadsheetKey = createSpreadsheet(TESTING_SPREADSHEET_NAME);
		String obtainedSpreadsheetKey = driveHelper.getNewestSpreadsheetKey(TESTING_SPREADSHEET_NAME);

		assertEquals(expectedSpreadsheetKey, obtainedSpreadsheetKey);
	}

	public void testGetSpreadsheetUpdateTime() {
		final int TIME_DELTA_IN_SECONDS = 60;

		InternetDateTime timeBeforeCreation = addSecondsToDateTime(new InternetDateTime(),
			-TIME_DELTA_IN_SECONDS);

		String spreadsheetKey = createSpreadsheet(TESTING_SPREADSHEET_NAME);
		InternetDateTime spreadsheetModifiedTime = driveHelper.getSpreadsheetUpdateTime(spreadsheetKey);

		InternetDateTime timeAfterCreation = addSecondsToDateTime(new InternetDateTime(),
			TIME_DELTA_IN_SECONDS);

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
