package app.android.simpleflashcards.spreadsheets.models;


import com.google.api.client.util.Key;


public class Cell
{
	@Key("@row")
	public int row;

	@Key("@col")
	public int column;

	@Key("@inputValue")
	public String value;
}
