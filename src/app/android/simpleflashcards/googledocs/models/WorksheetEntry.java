package app.android.simpleflashcards.googledocs.models;


import java.util.List;

import app.android.simpleflashcards.googledocs.SpreadsheetUrl;

import com.google.api.client.util.Key;


public class WorksheetEntry
{
	private static final String SELF_SCHEMA = "self";
	private static final String EDIT_SCHEMA = "edit";
	private static final String CELL_FEED_SCHEMA = "http://schemas.google.com/spreadsheets/2006#cellsfeed";

	@Key
	private String title;

	@Key("gs:rowCount")
	private int rowCount;

	@Key("gs:colCount")
	private int columnCount;

	@Key("link")
	private LinksList links;

	public WorksheetEntry() {
		links = new LinksList();
	}

	public WorksheetEntry(String title, int rowCount, int columnCount) {
		this();

		this.title = title;
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public List<Link> getLinks() {
		return links;
	}

	public SpreadsheetUrl getSelfFeedUrl() {
		return new SpreadsheetUrl(links.findFirstWithRel(SELF_SCHEMA).getHref());
	}

	public SpreadsheetUrl getEditUrl() {
		return new SpreadsheetUrl(links.findFirstWithRel(EDIT_SCHEMA).getHref());
	}

	public SpreadsheetUrl getCellFeedUrl() {
		return new SpreadsheetUrl(links.findFirstWithRel(CELL_FEED_SCHEMA).getHref());
	}

	public SpreadsheetUrl getCellEditUrl(int row, int column) {
		return new SpreadsheetUrl(String.format("%s/R%dC%d", getCellFeedUrl().toString(), row, column));
	}
}
