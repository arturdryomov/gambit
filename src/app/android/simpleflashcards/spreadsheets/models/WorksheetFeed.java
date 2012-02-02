package app.android.simpleflashcards.spreadsheets.models;


import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;


public class WorksheetFeed
{
	@Key("title")
	public String title;

	@Key("author")
	public Author author;

	@Key("entry")
	public List<WorksheetEntry> worksheets = new ArrayList<WorksheetEntry>();

	public List<WorksheetEntry> getEntries() {
		return worksheets;
	}
}
