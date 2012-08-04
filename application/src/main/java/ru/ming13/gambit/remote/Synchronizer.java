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

package ru.ming13.gambit.remote;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ru.ming13.gambit.local.Card;
import ru.ming13.gambit.local.DbProvider;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.local.Decks;


public class Synchronizer
{
	private static final String SYNC_SPREADSHEET_NAME = "Gambit flashcards";

	private final DriveHelper driveHelper;
	private final RemoteDecksConverter remoteDecksConverter;

	public Synchronizer(String authToken, String apiKey) {
		driveHelper = new DriveHelper(authToken, apiKey);
		remoteDecksConverter = new RemoteDecksConverter();
	}

	/**
	 * @throws NothingToSyncException if there are no local and remote data to sync.
	 * @throws UnauthorizedException if something wrong with authorization.
	 * @throws SyncException if general error had happened.
	 */
	public String sync(String spreadsheetKey) {
		try {
			checkSpreadsheetForTrashing(spreadsheetKey);
			trySync(spreadsheetKey);

			return spreadsheetKey;
		}
		catch (SpreadsheetNotExistsException e) {
			return sync();
		}
		catch (DecksNotFoundException e) {
			return sync();
		}
	}

	private void checkSpreadsheetForTrashing(String spreadsheetKey) {
		if (driveHelper.isSpreadsheetTrashed(spreadsheetKey)) {
			throw new SpreadsheetNotExistsException();
		}
	}

	private void trySync(String spreadsheetKey) {
		if (isDriveSpreadsheetNewerThanLocalDatabase(spreadsheetKey)) {
			syncFromDriveSpreadsheetToLocalDatabase(spreadsheetKey);
		}
		else {
			syncFromLocalDatabaseToDriveSpreadsheet(spreadsheetKey);
		}
	}

	private boolean isDriveSpreadsheetNewerThanLocalDatabase(String spreadsheetKey) {
		InternetDateTime driveSpreadsheetModifiedTime = driveHelper.getSpreadsheetUpdateTime(
			spreadsheetKey);
		InternetDateTime localDatabaseModifiedTime = DbProvider.getInstance().getDecks().getLastUpdatedDateTime();

		return driveSpreadsheetModifiedTime.isAfter(localDatabaseModifiedTime);
	}

	private void syncFromDriveSpreadsheetToLocalDatabase(String spreadsheetKey) {
		InputStream xlsData = driveHelper.downloadXlsData(spreadsheetKey);
		List<RemoteDeck> remoteDecks = remoteDecksConverter.fromXlsData(xlsData);
		writeRemoteDecksToLocalDatabase(remoteDecks);
	}

	private void writeRemoteDecksToLocalDatabase(List<RemoteDeck> remoteDecks) {
		Decks localDecks = DbProvider.getInstance().getDecks();

		localDecks.beginTransaction();
		try {
			tryWriteRemoteDecksToLocalDatabase(remoteDecks);
			localDecks.setTransactionSuccessful();
		}
		finally {
			localDecks.endTransaction();
		}
	}

	private void tryWriteRemoteDecksToLocalDatabase(List<RemoteDeck> remoteDecks) {
		Decks localDecks = DbProvider.getInstance().getDecks();

		localDecks.clear();

		for (RemoteDeck remoteDeck : remoteDecks) {
			Deck localDeck = localDecks.createDeck(remoteDeck.getTitle());

			for (RemoteCard remoteCard : remoteDeck.getCards()) {
				localDeck.createCard(remoteCard.getFrontSideText(), remoteCard.getBackSideText());
			}
		}
	}

	private void syncFromLocalDatabaseToDriveSpreadsheet(String spreadsheetKey) {
		List<RemoteDeck> remoteDecks = readRemoteDecksFromLocalDatabase();
		byte[] xlsData = remoteDecksConverter.toXlsData(remoteDecks);
		driveHelper.updateSpreadsheet(spreadsheetKey, xlsData);
	}

	private List<RemoteDeck> readRemoteDecksFromLocalDatabase() {
		List<RemoteDeck> remoteDecks = new ArrayList<RemoteDeck>();
		List<Deck> localDecks = DbProvider.getInstance().getDecks().getDecksList();

		for (Deck localDeck : localDecks) {
			List<RemoteCard> remoteCards = new ArrayList<RemoteCard>();

			for (Card localCard : localDeck.getCardsList()) {
				remoteCards.add(buildRemoteCard(localCard));
			}

			remoteDecks.add(buildRemoteDeck(localDeck, remoteCards));
		}

		return remoteDecks;
	}

	private RemoteCard buildRemoteCard(Card localCard) {
		RemoteCard remoteCard = new RemoteCard();

		remoteCard.setFrontSideText(localCard.getFrontSideText());
		remoteCard.setBackSideText(localCard.getBackSideText());

		return remoteCard;
	}

	private RemoteDeck buildRemoteDeck(Deck localDeck, List<RemoteCard> remoteCards) {
		RemoteDeck remoteDeck = new RemoteDeck();

		remoteDeck.setTitle(localDeck.getTitle());
		remoteDeck.setCards(remoteCards);

		return remoteDeck;
	}

	/**
	 * @throws NothingToSyncException if there are no local and remote data to sync.
	 * @throws UnauthorizedException if something wrong with authorization.
	 * @throws SyncException if general error had happened.
	 */
	public String sync() {
		String spreadsheetKey;

		try {
			spreadsheetKey = driveHelper.getNewestSpreadsheetKey(SYNC_SPREADSHEET_NAME);

			syncFirstTimeToExistingDriveSpreadsheet(spreadsheetKey);
		}
		catch (SpreadsheetNotExistsException e) {
			spreadsheetKey = syncToNewDriveSpreadsheet();
		}

		return spreadsheetKey;
	}

	private void syncFirstTimeToExistingDriveSpreadsheet(String spreadsheetKey) {
		if (DbProvider.getInstance().getDecks().getDecksList().isEmpty()) {
			syncFromDriveSpreadsheetToLocalDatabase(spreadsheetKey);
		}
		else {
			sync(spreadsheetKey);
		}
	}

	private String syncToNewDriveSpreadsheet() {
		List<RemoteDeck> remoteDecks = readRemoteDecksFromLocalDatabase();

		if (remoteDecks.isEmpty()) {
			throw new NothingToSyncException();
		}

		byte[] xlsData = remoteDecksConverter.toXlsData(remoteDecks);

		return driveHelper.createSpreadsheet(SYNC_SPREADSHEET_NAME, xlsData);
	}
}
