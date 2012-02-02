package app.android.simpleflashcards.spreadsheets.models;


import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;


public class CellFeed
{
	@Key("title")
	public String title;

	@Key("author")
	public Author author;

	@Key("entry")
	public List<CellEntry> cells = new ArrayList<CellEntry>();

	public List<CellEntry> getEntries() {
		return cells;
	}
}
