package app.android.simpleflashcards.googledocs.models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.android.simpleflashcards.Rfc3339DateProcessor;
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
	private List<Link> links = new ArrayList<Link>();

	@Key("updated")
	private String lastUpdatedTime;

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
		return Rfc3339DateProcessor.parse(lastUpdatedTime);
	}

	public String getKey() {
		KeyUrl keyUrl = new KeyUrl(Link.findFirstWithRel(links, ALTERNATE_SCHEME).getHref());
		return keyUrl.getKey();
	}

	public SpreadsheetUrl getSelfFeedUrl() {
		return new SpreadsheetUrl(Link.findFirstWithRel(links, SELF_SCHEMA).getHref());
	}

	public SpreadsheetUrl getWorksheetFeedUrl() {
		return new SpreadsheetUrl(getContent().getSource());
	}

	public String getLinkHref(String linkRel) {
		return Link.findFirstWithRel(links, linkRel).getHref();
	}
}
