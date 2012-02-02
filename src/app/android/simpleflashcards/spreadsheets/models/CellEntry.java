package app.android.simpleflashcards.spreadsheets.models;


import com.google.api.client.util.Key;

public class CellEntry
{
	@Key("gs:cell")
	public Cell cell;

	@Key
	public String content;
}
