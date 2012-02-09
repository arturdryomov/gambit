package app.android.simpleflashcards.googledocs.models;


import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;


public class DocumentFeed
{
	@Key("entry")
	private List<DocumentEntry> entries = new ArrayList<DocumentEntry>();

	public List<DocumentEntry> getEntries() {
		return entries;
	}
}
