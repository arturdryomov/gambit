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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
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
		return buildRemoteDecks(buildWorkbook(xlsData));
	}

	private Workbook buildWorkbook(InputStream xlsData) {
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

	private List<RemoteDeck> buildRemoteDecks(Workbook workbook) {
		List<RemoteDeck> remoteDecks = new ArrayList<RemoteDeck>();

		if (workbook.getNumberOfSheets() == 0) {
			return remoteDecks;
		}

		for (Sheet sheet : workbook.getSheets()) {
			remoteDecks.add(buildRemoteDeck(sheet));
		}

		return remoteDecks;
	}

	private RemoteDeck buildRemoteDeck(Sheet sheet) {
		RemoteDeck remoteDeck = new RemoteDeck();

		remoteDeck.setTitle(sheet.getName());
		remoteDeck.setCards(buildRemoteCards(sheet));

		return remoteDeck;
	}

	private List<RemoteCard> buildRemoteCards(Sheet sheet) {
		List<RemoteCard> remoteCards = new ArrayList<RemoteCard>();

		if( (sheet.getColumns() < MINIMAL_COLUMNS_COUNT) || (sheet.getRows() < MINIMAL_ROWS_COUNT)) {
			return remoteCards;
		}

		for (int rowIndex = CARDS_FIRST_ROW_INDEX; rowIndex < sheet.getRows(); rowIndex++) {
			Cell[] row = sheet.getRow(rowIndex);

			if (!isRowEmpty(row)) {
				remoteCards.add(buildRemoteCard(row));
			}
		}

		return remoteCards;
	}

	private boolean isRowEmpty(Cell[] row) {
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

	private RemoteCard buildRemoteCard(Cell[] row) {
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
			throw new DecksNotFoundException();
		}

		for (int deckIndex = 0; deckIndex < remoteDecks.size(); deckIndex++) {
			RemoteDeck remoteDeck = remoteDecks.get(deckIndex);

			WritableSheet sheet = workbook.createSheet(remoteDeck.getTitle(), deckIndex);
			fillSheet(sheet, remoteDeck);
			expandCardColumns(sheet);
		}
	}

	private void expandCardColumns(WritableSheet sheet) {
		expandColumn(sheet, FRONT_SIDE_COLUMN_INDEX);
		expandColumn(sheet, BACK_SIDE_COLUMN_INDEX);
	}

	private void expandColumn(WritableSheet sheet, int columnIndex) {
		CellView columnView = sheet.getColumnView(columnIndex);
		columnView.setAutosize(true);
		sheet.setColumnView(columnIndex, columnView);
	}

	private void fillSheet(WritableSheet sheet, RemoteDeck remoteDeck) {
		insertHeader(sheet);
		insertCards(sheet, remoteDeck);
	}

	private void insertHeader(WritableSheet sheet) {
		try {
			WritableCell frontSideColumnHeader = new Label(FRONT_SIDE_COLUMN_INDEX, HEADER_ROW_INDEX,
				HEADER_FRONT_SIDE);
			frontSideColumnHeader.setCellFormat(buildBoldCellFormat());
			sheet.addCell(frontSideColumnHeader);

			WritableCell backSideColumnHeader = new Label(BACK_SIDE_COLUMN_INDEX, HEADER_ROW_INDEX,
				HEADER_BACK_SIDE);
			backSideColumnHeader.setCellFormat(buildBoldCellFormat());
			sheet.addCell(backSideColumnHeader);
		}
		catch (WriteException e) {
			throw new ConvertingException();
		}
	}

	private CellFormat buildBoldCellFormat() {
		try {
			WritableCellFormat cellFormat = new WritableCellFormat();
			WritableFont font = new WritableFont(cellFormat.getFont());

			font.setBoldStyle(WritableFont.BOLD);
			cellFormat.setFont(font);

			return cellFormat;
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
