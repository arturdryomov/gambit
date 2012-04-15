package app.android.simpleflashcards.remotedecks;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import app.android.simpleflashcards.InternetDateTime;
import app.android.simpleflashcards.googledocs.SpreadsheetsClient;
import app.android.simpleflashcards.googledocs.models.Cell;
import app.android.simpleflashcards.googledocs.models.CellEntry;
import app.android.simpleflashcards.googledocs.models.SpreadsheetEntry;
import app.android.simpleflashcards.googledocs.models.WorksheetEntry;


public class RemoteDecks
{
	private static final String FRONT_SIDE_TEXT = "Front Side Text";
	private static final String BACK_SIDE_TEXT = "Back Side Text";
	private static final int FRONT_SIDE_TEXT_COLUMN_INDEX = 1;
	private static final int BACK_SIDE_TEXT_COLUMN_INDEX = 2;
	private static final int TABLE_CAPTION_ROW_INDEX = 1;
	private static final int FIRST_CARD_ROW_INDEX = TABLE_CAPTION_ROW_INDEX + 1;

	private SpreadsheetsClient spreadsheetClient;
	private SpreadsheetEntry spreadsheetEntry;
	private String spreadsheetKey;

	private class CardTexts
	{
		public String front;
		public String back;

		public CardTexts() {
			front = new String();
			back = new String();
		}

		public boolean isValid() {
			return !front.isEmpty() && !back.isEmpty();
		}
	}

	public RemoteDecks(String authToken, String spreadsheetKey) {
		this.spreadsheetClient = new SpreadsheetsClient(authToken);
		this.spreadsheetKey = spreadsheetKey;
	}

	public InternetDateTime lastUpdatedDateTime() {
		ensureSpreadsheetEntryDownloaded();
		return spreadsheetEntry.getLastUpdatedDateTime();
	}

	private void ensureSpreadsheetEntryDownloaded() {
		if (spreadsheetEntry == null) {
			downloadSpreadsheetEntry();
		}
	}

	private void downloadSpreadsheetEntry() {
		spreadsheetEntry = spreadsheetClient.getSpreadsheetEntryByKey(spreadsheetKey);
	}

	public List<RemoteDeck> getDecks() {
		ensureSpreadsheetEntryDownloaded();

		List<RemoteDeck> decks = new ArrayList<RemoteDeck>();

		for (WorksheetEntry worksheetEntry : getWorksheetEntries()) {
			decks.add(remoteDeckFromWorksheetEntry(worksheetEntry));
		}

		return decks;
	}

	private List<WorksheetEntry> getWorksheetEntries() {
		return spreadsheetClient.getWorksheetFeed(spreadsheetEntry).getEntries();
	}

	private RemoteDeck remoteDeckFromWorksheetEntry(WorksheetEntry worksheetEntry) {
		List<CellEntry> cellEntries = spreadsheetClient.getCellFeed(worksheetEntry).getEntries();

		SortedMap<Integer, CardTexts> cardTextsMap = new TreeMap<Integer, CardTexts>();

		for (CellEntry cellEntry : cellEntries) {
			Cell cell = cellEntry.getCell();
			if (cellContainsValidCardText(cell)) {
				cardTextsMap = addCellValueToMap(cardTextsMap, cell);
			}
		}

		RemoteDeck deck = new RemoteDeck();

		deck.setCards(cardsListFromCardTextsMap(cardTextsMap));
		deck.setTitle(worksheetEntry.getTitle());

		return deck;
	}

	private boolean cellContainsValidCardText(Cell cell) {
		if (cell.getRow() < FIRST_CARD_ROW_INDEX) {
			return false;
		}

		if ((cell.getColumn() != FRONT_SIDE_TEXT_COLUMN_INDEX)
			&& (cell.getColumn() != BACK_SIDE_TEXT_COLUMN_INDEX)) {
			return false;
		}

		return !cell.getValue().isEmpty();
	}

	private SortedMap<Integer, CardTexts> addCellValueToMap(
		SortedMap<Integer, CardTexts> cardTextsMap, Cell cell) {

		SortedMap<Integer, CardTexts> newCardTextsMap = new TreeMap<Integer, RemoteDecks.CardTexts>(
			cardTextsMap);

		if (!newCardTextsMap.containsKey(cell.getRow())) {
			newCardTextsMap.put(cell.getRow(), new CardTexts());
		}

		if (cell.getColumn() == FRONT_SIDE_TEXT_COLUMN_INDEX) {
			newCardTextsMap.get(cell.getRow()).front = cell.getValue();
		}
		else if (cell.getColumn() == BACK_SIDE_TEXT_COLUMN_INDEX) {
			newCardTextsMap.get(cell.getRow()).back = cell.getValue();
		}
		else {
			throw new RemoteDecksException("Invalid column index");
		}

		return newCardTextsMap;
	}

	private List<RemoteCard> cardsListFromCardTextsMap(SortedMap<Integer, CardTexts> cardTextsMap) {
		List<RemoteCard> cards = new ArrayList<RemoteCard>();

		for (Map.Entry<Integer, CardTexts> entry : cardTextsMap.entrySet()) {
			CardTexts cardTexts = entry.getValue();

			if (cardTexts.isValid()) {
				RemoteCard card = new RemoteCard();
				card.setFrontSideText(cardTexts.front);
				card.setBackSideText(cardTexts.back);
				cards.add(card);
			}
		}

		return cards;
	}

	public void setDecks(List<RemoteDeck> decks) {
		// TODO: Google Spreadsheets need to have at least one worksheet in a spreadsheet.
		// Possibly we should ask user to have at least one deck?
		if (decks.isEmpty()) {
			return;
		}

		ensureSpreadsheetEntryDownloaded();

		createWorksheetStructureForDecks(decks);

		List<WorksheetEntry> worksheetEntries = getWorksheetEntries();

		for (int i = 0; i < decks.size(); i++) {
			writeDeckToWorksheet(decks.get(i), worksheetEntries.get(i));
		}

		invalidateSpreadsheetEntry();
	}

	private void createWorksheetStructureForDecks(List<RemoteDeck> decks) {
		List<WorksheetEntry> worksheetEntries = getWorksheetEntries();

		// Remove all worksheets but the last one
		while (worksheetEntries.size() > 1) {
			spreadsheetClient.deleteWorksheet(worksheetEntries.get(0));
			worksheetEntries.remove(0);
		}

		int columnCount = Math.max(FRONT_SIDE_TEXT_COLUMN_INDEX, BACK_SIDE_TEXT_COLUMN_INDEX);

		// Insert a worksheet for every deck
		for (RemoteDeck deck : decks) {
			// TODO: Make sure rowCount not exeeds max possible row count
			int rowCount = deck.getCards().size() * 2;
			spreadsheetClient.insertWorksheet(spreadsheetEntry, deck.getTitle(), rowCount, columnCount);
		}

		// Remove the old worksheet which could no be deleted previously
		spreadsheetClient.deleteWorksheet(worksheetEntries.get(0));
	}

	private void writeDeckToWorksheet(RemoteDeck remoteDeck, WorksheetEntry worksheetEntry) {
		// Create table title
		spreadsheetClient.updateCell(worksheetEntry, TABLE_CAPTION_ROW_INDEX,
			FRONT_SIDE_TEXT_COLUMN_INDEX, FRONT_SIDE_TEXT);
		spreadsheetClient.updateCell(worksheetEntry, TABLE_CAPTION_ROW_INDEX,
			BACK_SIDE_TEXT_COLUMN_INDEX, BACK_SIDE_TEXT);

		// Write cards
		for (int i = 0; i < remoteDeck.getCards().size(); i++) {
			int row = i + FIRST_CARD_ROW_INDEX;
			writeCardToRow(remoteDeck.getCards().get(i), row, worksheetEntry);
		}
	}

	private void writeCardToRow(RemoteCard remoteCard, int row, WorksheetEntry worksheetEntry) {
		spreadsheetClient.updateCell(worksheetEntry, row, FRONT_SIDE_TEXT_COLUMN_INDEX,
			remoteCard.getFrontSideText());
		spreadsheetClient.updateCell(worksheetEntry, row, BACK_SIDE_TEXT_COLUMN_INDEX,
			remoteCard.getBackSideText());
	}

	private void invalidateSpreadsheetEntry() {
		spreadsheetEntry = null;
	}
}
