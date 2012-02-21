package app.android.simpleflashcards.googledocs.models;


import java.util.List;

import com.google.api.client.util.Key;


public class Link
{
	@Key("@href")
	private String href;

	@Key("@rel")
	private String rel;

	public String getHref() {
		return href;
	}

	public String getRel() {
		return rel;
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
