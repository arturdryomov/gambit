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

package ru.ming13.gambit.test;


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
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;
import com.google.api.services.drive.model.File;
import ru.ming13.gambit.remote.convert.ConvertingException;
import ru.ming13.gambit.remote.drive.DriveHelper;
import ru.ming13.gambit.remote.InternetDateTime;
import ru.ming13.gambit.remote.model.RemoteCard;
import ru.ming13.gambit.remote.model.RemoteDeck;
import ru.ming13.gambit.remote.convert.RemoteDecksConverter;
import ru.ming13.gambit.remote.SpreadsheetNotExistsException;
import ru.ming13.gambit.ui.activity.DeckCreationActivity;
import ru.ming13.gambit.ui.account.AccountSelector;
import ru.ming13.gambit.ui.account.GoogleDriveAuthorizer;


public class DriveHelperTests extends InstrumentationTestCase
{
	private static final String TESTING_SPREADSHEET_NAME = "Spreadsheet for Gambit testing purposes";

	private static final String MIME_GOOGLE_SPREADSHEET = "application/vnd.google-apps.spreadsheet";

	private static String authToken;
	private static Activity hostActivity;

	private DriveHelper driveHelper;
	private Drive driveService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ensureAuthorized();

		driveHelper = new DriveHelper(authToken, getApiKey());
		driveService = buildDriveService();
	}

	private void ensureAuthorized() {
		if (hostActivity == null) {
			hostActivity = launchActivity("ru.ming13.gambit", DeckCreationActivity.class, null);
		}

		if (authToken == null) {
			Account account = AccountSelector.select(hostActivity);

			authToken = GoogleDriveAuthorizer.getToken(hostActivity, account);
			GoogleDriveAuthorizer.invalidateToken(hostActivity, authToken);
			authToken = GoogleDriveAuthorizer.getToken(hostActivity, account);
		}
	}

	private String getApiKey() {
		return hostActivity.getString(ru.ming13.gambit.R.string.google_api_key);
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
			catch (SpreadsheetNotExistsException e) {
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
		String spreadsheetKey = createTestingSpreadsheet();

		try {
			driveHelper.updateSpreadsheet(spreadsheetKey, xlsData);
		}
		catch (RuntimeException e) {
			fail();
		}
	}

	private String createTestingSpreadsheet() {
		File spreadsheetFile = new File();
		spreadsheetFile.setTitle(TESTING_SPREADSHEET_NAME);
		spreadsheetFile.setMimeType(MIME_GOOGLE_SPREADSHEET);

		try {
			return driveService.files().insert(spreadsheetFile).execute().getId();
		}
		catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void testDownloadXlsData() {
		String spreadsheetKey = createTestingSpreadsheet();

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
		String expectedSpreadsheetKey = createTestingSpreadsheet();
		String obtainedSpreadsheetKey = driveHelper.getNewestSpreadsheetKey(TESTING_SPREADSHEET_NAME);

		assertEquals(expectedSpreadsheetKey, obtainedSpreadsheetKey);
	}

	public void testGetSpreadsheetUpdateTime() {
		final int TIME_DELTA_IN_SECONDS = 60;

		InternetDateTime timeBeforeCreation = addSecondsToDateTime(new InternetDateTime(),
			-TIME_DELTA_IN_SECONDS);

		String spreadsheetKey = createTestingSpreadsheet();
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
