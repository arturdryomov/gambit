package app.android.gambit.remote;



import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.xml.atom.AtomContent;


public class SpreadsheetsClient extends GoogleDocsClient
{
	public SpreadsheetsClient(String authToken) {
		super(authToken);
	}

	public SpreadsheetFeed getSpreadsheetFeed() {
		HttpRequest request = buildGetRequest(SpreadsheetUrl.spreadsheetFeedUrl());
		return processGetRequest(request, SpreadsheetFeed.class);
	}

	public SpreadsheetEntry getSpreadsheetEntry(String id) {
		HttpRequest request = buildGetRequest(new SpreadsheetUrl(id));
		return processGetRequest(request, SpreadsheetEntry.class);
	}

	public SpreadsheetEntry getSpreadsheetEntryByKey(String key) {
		SpreadsheetFeed spreadsheets = getSpreadsheetFeed();

		for (SpreadsheetEntry spreadsheet : spreadsheets.getEntries()) {
			if (spreadsheet.getKey().equals(key)) {
				return spreadsheet;
			}
		}

		throw new EntryNotFoundException();
	}

	public WorksheetFeed getWorksheetFeed(SpreadsheetEntry spreadsheet) {
		HttpRequest request = buildGetRequest(spreadsheet.getWorksheetFeedUrl());
		return processGetRequest(request, WorksheetFeed.class);
	}

	public CellFeed getCellFeed(WorksheetEntry worksheet) {
		HttpRequest request = buildGetRequest(worksheet.getCellFeedUrl());
		return processGetRequest(request, CellFeed.class);
	}

	public void insertWorksheet(SpreadsheetEntry spreadsheet, String title, int rowCount,
		int columnCount) {

		WorksheetEntry entry = new WorksheetEntry(title, rowCount, columnCount);
		AtomContent content = AtomContent.forEntry(getXmlNamespaceDictionary(), entry);

		HttpRequest request = buildPostRequest(spreadsheet.getWorksheetFeedUrl(), content);
		processPostRequest(request);
	}

	public void updateWorksheet(WorksheetEntry worksheet) {
		AtomContent content = AtomContent.forEntry(getXmlNamespaceDictionary(), worksheet);

		HttpRequest request = buildPutRequest(worksheet.getEditUrl(), content);
		processPutRequest(request);
	}

	public void deleteWorksheet(WorksheetEntry worksheet) {
		HttpRequest request = buildDeleteRequest(worksheet.getEditUrl());
		processDeleteRequest(request);
	}

	public void clearWorksheet(WorksheetEntry worksheet) {
		// First, clear all cells except the first one by removing them from the worksheet
		// and then adding them back.
		// After that just clear the first cell.
		// This is faster than clearing cells one by one.

		int rowCount = worksheet.getRowCount();
		int columnCount = worksheet.getColumnCount();

		worksheet.setRowCount(1);
		worksheet.setColumnCount(1);
		updateWorksheet(worksheet);

		worksheet.setRowCount(rowCount);
		worksheet.setColumnCount(columnCount);
		updateWorksheet(worksheet);

		updateCell(worksheet, 1, 1, new String());
	}

	public void updateCell(WorksheetEntry worksheet, int row, int column, String value) {
		Cell cell = new Cell(row, column, value);
		CellEntry cellEntry = new CellEntry(worksheet, cell);

		AtomContent content = AtomContent.forEntry(getXmlNamespaceDictionary(), cellEntry);
		HttpRequest request = buildPutRequest(worksheet.getCellEditUrl(row, column), content);

		processPutRequest(request);
	}
}
