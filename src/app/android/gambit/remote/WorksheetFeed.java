package app.android.gambit.remote;


import java.util.List;

import com.google.api.client.util.Key;


class WorksheetFeed
{
	@Key("title")
	private String title;

	@Key("author")
	private Author author;

	@Key("entry")
	private List<WorksheetEntry> worksheets;

	public String getTitle() {
		return title;
	}

	public Author getAuthor() {
		return author;
	}

	public List<WorksheetEntry> getEntries() {
		return worksheets;
	}
}