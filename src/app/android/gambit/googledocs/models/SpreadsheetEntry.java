package app.android.gambit.googledocs.models;


import java.util.List;

import app.android.gambit.InternetDateTime;
import app.android.gambit.googledocs.KeyUrl;
import app.android.gambit.googledocs.SpreadsheetUrl;

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
	private String lastUpdatedDateTime;

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

	public InternetDateTime getLastUpdatedDateTime() {
		return new InternetDateTime(lastUpdatedDateTime);
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
