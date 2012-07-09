package app.android.gambit.remote;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.android.gambit.InternetDateTime;
import app.android.gambit.local.Card;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import app.android.gambit.local.Decks;


public class Synchronizer
{
	private static final String SYNC_SPREADSHEET_NAME = "Gambit flashcards";

	private final GoogleDriveHelper driveHelper;
	private final RemoteDecksConverter remoteDecksConverter;

	public Synchronizer(String authToken, String apiKey) {
		driveHelper = new GoogleDriveHelper(authToken, apiKey);
		remoteDecksConverter = new RemoteDecksConverter();
	}

	public String sync(String spreadsheetKey) {
		try {
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

	public String sync() {
		String spreadsheetKey;

		try {
			spreadsheetKey = driveHelper.getNewestSpreadsheetKey(SYNC_SPREADSHEET_NAME);

			syncFirstTimeWithExistingDriveSpreadsheet(spreadsheetKey);
		}
		catch (SpreadsheetNotExistsException e) {
			spreadsheetKey = createDriveSpreadsheet();
		}

		return spreadsheetKey;
	}

	private void syncFirstTimeWithExistingDriveSpreadsheet(String spreadsheetKey) {
		if (DbProvider.getInstance().getDecks().getDecksList().isEmpty()) {
			syncFromDriveSpreadsheetToLocalDatabase(spreadsheetKey);
		}
		else {
			sync(spreadsheetKey);
		}
	}

	private String createDriveSpreadsheet() {
		List<RemoteDeck> remoteDecks = readRemoteDecksFromLocalDatabase();

		if (remoteDecks.isEmpty()) {
			throw new NothingToSyncException();
		}

		byte[] xlsData = remoteDecksConverter.toXlsData(remoteDecks);

		return driveHelper.createSpreadsheet(SYNC_SPREADSHEET_NAME, xlsData);
	}
}
