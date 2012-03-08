package app.android.simpleflashcards.googledocs.models;


import java.util.ArrayList;
import java.util.Collection;


public class LinksList extends ArrayList<Link>
{
	public LinksList() {
	}

	public LinksList(int initialCapacity) {
		super(initialCapacity);
	}

	public LinksList(Collection<Link> collection) {
		super(collection);
	}

	Link findFirstWithRel(String rel) {
		for (Link link : this) {
			if (rel.equals(link.getRel())) {
				return link;
			}
		}

		return new Link();
	}
}
