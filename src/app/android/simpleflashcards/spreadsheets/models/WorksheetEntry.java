package app.android.simpleflashcards.spreadsheets.models;


import java.util.ArrayList;
import java.util.List;

import app.android.simpleflashcards.spreadsheets.SpreadsheetUrl;

import com.google.api.client.util.Key;


public class WorksheetEntry
{
	private static final String SELF_SCHEMA = "self";
	private static final String CELL_FEED_SCHEMA = "http://schemas.google.com/spreadsheets/2006#cellsfeed";

	@Key
	public String title;

	@Key("gs:rowCount")
	public int rowCount;

	@Key("gs:colCount")
	public int columnCount;

	@Key("link")
	private List<Link> links = new ArrayList<Link>();

	public List<Link> getLinks() {
		return links;
	}

	public SpreadsheetUrl getSelfFeedUrl() {
		return new SpreadsheetUrl(Link.findFirstWithRel(links, SELF_SCHEMA).href);
	}

	public SpreadsheetUrl getCellFeedUrl() {
		return new SpreadsheetUrl(Link.findFirstWithRel(links, CELL_FEED_SCHEMA).href);
	}
}
