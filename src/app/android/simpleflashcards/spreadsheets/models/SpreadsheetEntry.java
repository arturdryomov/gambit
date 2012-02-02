package app.android.simpleflashcards.spreadsheets.models;


import java.util.ArrayList;
import java.util.List;

import app.android.simpleflashcards.spreadsheets.SpreadsheetUrl;

import com.google.api.client.util.Key;


public class SpreadsheetEntry
{
	private static final String SPREADSHEET_SELF_SCHEMA = "self";

	@Key
	public String id;

	@Key
	public String title;

	@Key
	public Author author;

	@Key
	public Content content;

	@Key("link")
	private List<Link> links = new ArrayList<Link>();

	public List<Link> getLinks() {
		return links;
	}

	public SpreadsheetUrl getSelfFeedUrl() {
		return new SpreadsheetUrl(Link.findFirstWithRel(links, SPREADSHEET_SELF_SCHEMA).href);
	}

	public SpreadsheetUrl getWorksheetFeedUrl() {
		return new SpreadsheetUrl(content.src);
	}
}
