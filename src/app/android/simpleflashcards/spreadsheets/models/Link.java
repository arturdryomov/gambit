package app.android.simpleflashcards.spreadsheets.models;


import java.util.List;

import com.google.api.client.util.Key;


public class Link
{
	@Key("@href")
	private String href = new String();

	@Key("@rel")
	private String rel = new String();

	public String getHref() {
		return href;
	}

	public String getRel() {
		return rel;
	}

	public boolean isEmpty() {
		return getHref().isEmpty() && getRel().isEmpty();
	}

	public static Link findFirstWithRel(List<Link> links, String rel) {
		for (Link link : links) {
			if (rel.equals(link.getRel())) {
				return link;
			}
		}

		return new Link();
	}
}
