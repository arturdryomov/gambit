package app.android.simpleflashcards.spreadsheets.models;


import java.util.List;

import com.google.api.client.util.Key;


public class Link
{
	@Key("@href")
	public String href = new String();

	@Key("@rel")
	public String rel = new String();

	public boolean isEmpty() {
		return href.isEmpty() && rel.isEmpty();
	}

	public static Link findFirstWithRel(List<Link> links, String rel) {
		for (Link link : links) {
			if (rel.equals(link.rel)) {
				return link;
			}
		}

		return new Link();
	}
}
