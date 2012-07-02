package app.android.gambit.remote;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


class RemoteDecksConverter
{
	private static final int CARDS_FIRST_ROW_INDEX = 1;

	private static final int CARD_FRONT_SIDE_COLUMN_INDEX = 0;
	private static final int CARD_BACK_SIDE_COLUMN_INDEX = 0;

	private static final int MINIMAL_COLUMNS_COUNT = 2;
	private static final int MINIMAL_LINES_COUNT = 2;

	public List<RemoteDeck> fromXlsData(InputStream xlsData) {
		return constructRemoteDecks(getSpreadsheet(xlsData));
	}

	private Workbook getSpreadsheet(InputStream xlsData) {
		try {
			return Workbook.getWorkbook(xlsData);
		}
		catch (IOException e) {
			throw new ConvertingException();
		}
		catch (BiffException e) {
			throw new ConvertingException();
		}
	}

	private List<RemoteDeck> constructRemoteDecks(Workbook spreadsheet) {
		List<RemoteDeck> remoteDecks = new ArrayList<RemoteDeck>();

		if (spreadsheet.getNumberOfSheets() == 0) {
			return remoteDecks;
		}

		for (Sheet sheet : spreadsheet.getSheets()) {
			remoteDecks.add(constructRemoteDeck(sheet));
		}

		return remoteDecks;
	}

	private RemoteDeck constructRemoteDeck(Sheet sheet) {
		RemoteDeck remoteDeck = new RemoteDeck();

		remoteDeck.setTitle(sheet.getName());
		remoteDeck.setCards(constructRemoteCards(sheet));

		return remoteDeck;
	}

	private List<RemoteCard> constructRemoteCards(Sheet sheet) {
		List<RemoteCard> remoteCards = new ArrayList<RemoteCard>();

		if (sheet.getColumns() < MINIMAL_COLUMNS_COUNT) {
			return remoteCards;
		}

		if (sheet.getRows() < MINIMAL_LINES_COUNT) {
			return remoteCards;
		}

		for (int rowIndex = CARDS_FIRST_ROW_INDEX; rowIndex < sheet.getRows(); rowIndex++) {
			remoteCards.add(constructRemoteCard(sheet.getRow(rowIndex)));
		}

		return remoteCards;
	}

	private RemoteCard constructRemoteCard(Cell[] row) {
		RemoteCard remoteCard = new RemoteCard();

		remoteCard.setFrontSideText(row[CARD_FRONT_SIDE_COLUMN_INDEX].getContents());
		remoteCard.setBackSideText(row[CARD_BACK_SIDE_COLUMN_INDEX].getContents());

		return remoteCard;
	}
}
