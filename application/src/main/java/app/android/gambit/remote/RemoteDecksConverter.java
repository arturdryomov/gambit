package app.android.gambit.remote;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class RemoteDecksConverter
{
	private static final int HEADER_ROW_INDEX = 0;
	private static final String HEADER_FRONT_SIDE = "Front Side Text";
	private static final String HEADER_BACK_SIDE = "Back Side Text";

	private static final int CARDS_FIRST_ROW_INDEX = HEADER_ROW_INDEX + 1;

	private static final int FRONT_SIDE_COLUMN_INDEX = 0;
	private static final int BACK_SIDE_COLUMN_INDEX = FRONT_SIDE_COLUMN_INDEX + 1;

	private static final int MINIMAL_COLUMNS_COUNT = 2;
	private static final int MINIMAL_ROWS_COUNT = 1;

	public List<RemoteDeck> fromXlsData(InputStream xlsData) {
		return constructRemoteDecks(getWorkbook(xlsData));
	}

	private Workbook getWorkbook(InputStream xlsData) {
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

	private List<RemoteDeck> constructRemoteDecks(Workbook workbook) {
		List<RemoteDeck> remoteDecks = new ArrayList<RemoteDeck>();

		if (workbook.getNumberOfSheets() == 0) {
			return remoteDecks;
		}

		for (Sheet sheet : workbook.getSheets()) {
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

		if (sheet.getRows() < MINIMAL_ROWS_COUNT) {
			return remoteCards;
		}

		for (int rowIndex = CARDS_FIRST_ROW_INDEX; rowIndex < sheet.getRows(); rowIndex++) {
			Cell[] row = sheet.getRow(rowIndex);

			if (!isCardDataEmpty(row)) {
				remoteCards.add(constructRemoteCard(row));
			}
		}

		return remoteCards;
	}

	private boolean isCardDataEmpty(Cell[] row) {
		if (row == null) {
			return true;
		}

		if (row.length == 0) {
			return true;
		}

		String frontSideText = row[FRONT_SIDE_COLUMN_INDEX].getContents();
		String backSideText = row[BACK_SIDE_COLUMN_INDEX].getContents();

		return TextUtils.isEmpty(frontSideText) && TextUtils.isEmpty(backSideText);
	}

	private RemoteCard constructRemoteCard(Cell[] row) {
		RemoteCard remoteCard = new RemoteCard();

		remoteCard.setFrontSideText(row[FRONT_SIDE_COLUMN_INDEX].getContents());
		remoteCard.setBackSideText(row[BACK_SIDE_COLUMN_INDEX].getContents());

		return remoteCard;
	}

	public byte[] toXlsData(List<RemoteDeck> remoteDecks) {
		ByteArrayOutputStream xlsDataStream = new ByteArrayOutputStream();

		WritableWorkbook workbook = createWorkbook(xlsDataStream);
		fillWorkbook(workbook, remoteDecks);
		saveWorkbook(workbook);

		return xlsDataStream.toByteArray();
	}

	private WritableWorkbook createWorkbook(OutputStream dataStream) {
		try {
			return Workbook.createWorkbook(dataStream);
		}
		catch (IOException e) {
			throw new ConvertingException();
		}
	}

	private void fillWorkbook(WritableWorkbook workbook, List<RemoteDeck> remoteDecks) {
		if (remoteDecks.isEmpty()) {
			return;
		}

		for (int deckIndex = 0; deckIndex < remoteDecks.size(); deckIndex++) {
			RemoteDeck remoteDeck = remoteDecks.get(deckIndex);

			WritableSheet sheet = workbook.createSheet(remoteDeck.getTitle(), deckIndex);
			fillSheet(sheet, remoteDeck);
		}
	}

	private void fillSheet(WritableSheet sheet, RemoteDeck remoteDeck) {
		insertHeader(sheet);
		insertCards(sheet, remoteDeck);
	}

	private void insertHeader(WritableSheet sheet) {
		try {
			sheet.addCell(new Label(FRONT_SIDE_COLUMN_INDEX, HEADER_ROW_INDEX, HEADER_FRONT_SIDE));
			sheet.addCell(new Label(BACK_SIDE_COLUMN_INDEX, HEADER_ROW_INDEX, HEADER_BACK_SIDE));
		}
		catch (WriteException e) {
			throw new ConvertingException();
		}
	}

	private void insertCards(WritableSheet sheet, RemoteDeck remoteDeck) {
		if (remoteDeck.getCards().isEmpty()) {
			return;
		}

		int currentRowIndex = CARDS_FIRST_ROW_INDEX;

		for (RemoteCard remoteCard : remoteDeck.getCards()) {
			insertCard(sheet, remoteCard, currentRowIndex);

			currentRowIndex++;
		}
	}

	private void insertCard(WritableSheet sheet, RemoteCard remoteCard, int rowIndex) {
		try {
			sheet.addCell(new Label(FRONT_SIDE_COLUMN_INDEX, rowIndex, remoteCard.getFrontSideText()));
			sheet.addCell(new Label(BACK_SIDE_COLUMN_INDEX, rowIndex, remoteCard.getBackSideText()));
		}
		catch (WriteException e) {
			throw new ConvertingException();
		}
	}

	private void saveWorkbook(WritableWorkbook workbook) {
		try {
			workbook.write();
			workbook.close();
		}
		catch (WriteException e) {
			throw new ConvertingException();
		}
		catch (IOException e) {
			throw new ConvertingException();
		}
	}
}
