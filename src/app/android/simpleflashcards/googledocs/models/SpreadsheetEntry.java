package app.android.simpleflashcards.googledocs.models;


import java.util.Date;
import java.util.List;

import app.android.simpleflashcards.InternetDateTimeFormatter;
import app.android.simpleflashcards.googledocs.KeyUrl;
import app.android.simpleflashcards.googledocs.SpreadsheetUrl;

import com.google.api.client.util.Key;


public class SpreadsheetEntry
{
	private static final String ALTERNATE_SCHEME = "alternate";
	private static final String SELF_SCHEMA = "self";

	@Key
	private String id;

	@Key
	private String title;

	@Key
	private Author author;

	@Key
	private Content content;

	@Key("link")
	private LinksList links;

	@Key("updated")
	private String lastUpdatedTime;

	public SpreadsheetEntry() {
		links = new LinksList();
	}

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

	public Date getLastUpdatedTime() {
		return InternetDateTimeFormatter.parse(lastUpdatedTime);
	}

	public String getKey() {
		KeyUrl keyUrl = new KeyUrl(links.findFirstWithRel(ALTERNATE_SCHEME).getHref());
		return keyUrl.getKey();
	}

	public SpreadsheetUrl getSelfFeedUrl() {
		return new SpreadsheetUrl(links.findFirstWithRel(SELF_SCHEMA).getHref());
	}

	public SpreadsheetUrl getWorksheetFeedUrl() {
		return new SpreadsheetUrl(getContent().getSource());
	}

	public String getLinkHref(String linkRel) {
		return links.findFirstWithRel(linkRel).getHref();
	}
}
