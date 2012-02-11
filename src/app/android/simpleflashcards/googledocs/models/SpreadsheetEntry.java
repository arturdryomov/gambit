package app.android.simpleflashcards.googledocs.models;


import java.util.ArrayList;
import java.util.List;

import app.android.simpleflashcards.googledocs.SpreadsheetUrl;

import com.google.api.client.util.Key;


public class SpreadsheetEntry
{
	private static final String SPREADSHEET_SELF_SCHEMA = "self";

	@Key
	private String id;

	@Key
	private String title;

	@Key
	private Author author;

	@Key
	private Content content;

	@Key("link")
	private List<Link> links = new ArrayList<Link>();

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Author getAuthor() {
		return author;
	}

	public Content getContent() {
		return content;
	}

	public List<Link> getLinks() {
		return links;
	}

	public SpreadsheetUrl getSelfFeedUrl() {
		return new SpreadsheetUrl(Link.findFirstWithRel(links, SPREADSHEET_SELF_SCHEMA).getHref());
	}

	public SpreadsheetUrl getWorksheetFeedUrl() {
		return new SpreadsheetUrl(getContent().getSource());
	}
}
