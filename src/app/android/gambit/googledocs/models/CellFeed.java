package app.android.gambit.googledocs.models;


import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;


public class CellFeed
{
	@Key("title")
	private String title;

	@Key("author")
	private Author author;

	@Key("entry")
	private List<CellEntry> cells;

	public CellFeed() {
		cells = new ArrayList<CellEntry>();
	}

	public String getTitle() {
		return title;
	}

	public Author getAuthor() {
		return author;
	}

	public List<CellEntry> getEntries() {
		return cells;
	}
}
