package app.android.simpleflashcards.spreadsheets.models;


import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;


public class SpreadsheetFeed
{
	@Key("entry")
	private List<SpreadsheetEntry> entries = new ArrayList<SpreadsheetEntry>();

	public List<SpreadsheetEntry> getEntries() {
		return entries;
	}
}
