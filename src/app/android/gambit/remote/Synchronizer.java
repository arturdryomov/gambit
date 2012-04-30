package app.android.gambit.remote;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import app.android.gambit.InternetDateTime;
import app.android.gambit.local.Card;
import app.android.gambit.local.DatabaseProvider;
import app.android.gambit.local.Deck;
import app.android.gambit.local.Decks;
import app.android.gambit.remote.DocumentEntry.Type;


public class Synchronizer
{
	private static final int ACCEPTABLE_TIME_DELTA_IN_MILLIS = 45 * 1000; // 45 seconds

	public String createSpreadsheet(String spreadsheetTitle, String googleDocsAuthToken) {
		DocumentsListClient client = new DocumentsListClient(googleDocsAuthToken);
		client.uploadEmptyDocument(Type.SPREADSHEET, spreadsheetTitle);

		return findJustInsertedSpeadsheetKey(spreadsheetTitle, googleDocsAuthToken);
	}

	private String findJustInsertedSpeadsheetKey(String spreadsheetTitle, String googleDocsAuthToken) {
		/* Simply take first spreadsheet with proper title that was changed (assumed 'created')
		 * no earlier than ACCEPTABLE_TIME_DELTA_IN_MILLIS milliseconds ago.
		 *
		 * No very determined but nothing better is provided with Documents List API.
		 */

		DocumentsListClient client = new DocumentsListClient(googleDocsAuthToken);

		List<DocumentEntry> documentEntries = client.getDocumentFeed(Type.SPREADSHEET).getEntries();

		Comparator<DocumentEntry> comparator = Collections
			.reverseOrder(new DocumentEntriesComparator());
		Collections.sort(documentEntries, comparator);

		for (DocumentEntry entry : documentEntries) {
			if (entry.getTitle().equals(spreadsheetTitle)) {
				if (isLastUpdateDateTimeAcceptable(entry)) {
					return entry.getSpreadsheetKey();
				}
			}
		}

		throw new SyncException();
	}

	private final class DocumentEntriesComparator implements Comparator<DocumentEntry>
	{
		@Override
		public int compare(DocumentEntry first, DocumentEntry second) {
			Date leftDate = first.getLastUpdatedDateTime().toDate();
			Date rightDate = second.getLastUpdatedDateTime().toDate();

			return leftDate.compareTo(rightDate);
		}
	}

	private boolean isLastUpdateDateTimeAcceptable(DocumentEntry entry) {
		long currentTimeInMillis = new Date().getTime();
		long entryTimeInMillis = entry.getLastUpdatedDateTime().toDate().getTime();

		return currentTimeInMillis - entryTimeInMillis <= ACCEPTABLE_TIME_DELTA_IN_MILLIS;
	}

	public void synchronize(String spreadsheetKey, String spreadsheetsToken) {
		RemoteDecks remoteDecks = new RemoteDecks(spreadsheetsToken, spreadsheetKey);

		InternetDateTime remoteDateTime = remoteDecks.lastUpdatedDateTime();
		InternetDateTime localDateTime = DatabaseProvider.getInstance().getDecks()
			.getLastUpdatedDateTime();

		if (localDateTime.isAfter(remoteDateTime)) {
			syncFromLocalToRemote(remoteDecks);
		}

		else if (remoteDateTime.isAfter(localDateTime)) {
			syncFromRemoteToLocal(remoteDecks);
		}
	}

	private void syncFromLocalToRemote(RemoteDecks remoteDecks) {
		remoteDecks.setDecks(createRemoteDeckListFromDatabase());

		// Update local last updated date-time in order to keep local data more 'fresh'.
		Decks decks = DatabaseProvider.getInstance().getDecks();
		decks.setCurrentDateTimeAsLastUpdated();
	}

	private List<RemoteDeck> createRemoteDeckListFromDatabase() {
		Decks decks = DatabaseProvider.getInstance().getDecks();
		List<RemoteDeck> remoteDeckList = new ArrayList<RemoteDeck>();

		for (Deck deck : decks.getDecksList()) {
			RemoteDeck remoteDeck = new RemoteDeck();

			remoteDeck.setTitle(deck.getTitle());
			remoteDeck.setCards(new ArrayList<RemoteCard>());

			for (Card card : deck.getCardsList()) {
				RemoteCard remoteCard = new RemoteCard();

				remoteCard.setFrontSideText(card.getFrontSideText());
				remoteCard.setBackSideText(card.getBackSideText());
				remoteDeck.getCards().add(remoteCard);
			}

			remoteDeckList.add(remoteDeck);
		}

		return remoteDeckList;
	}

	private void syncFromRemoteToLocal(RemoteDecks remoteDecks) {
		Decks decks = DatabaseProvider.getInstance().getDecks();

		decks.beginTransaction();
		try {
			trySyncFromRemoteToLocal(remoteDecks);
			decks.setTransactionSuccessful();
		}
		finally {
			decks.endTransaction();
		}
	}

	private void trySyncFromRemoteToLocal(RemoteDecks remoteDecks) {
		Decks decks = DatabaseProvider.getInstance().getDecks();

		decks.clear();

		for (RemoteDeck remoteDeck : remoteDecks.getDecks()) {
			Deck deck = decks.addNewDeck(remoteDeck.getTitle());

			for (RemoteCard remoteCard : remoteDeck.getCards()) {
				deck.addNewCard(remoteCard.getFrontSideText(), remoteCard.getBackSideText());
			}
		}
	}

}
