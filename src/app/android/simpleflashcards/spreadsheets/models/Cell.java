package app.android.simpleflashcards.spreadsheets.models;


import com.google.api.client.util.Key;


public class Cell
{
	@Key("@row")
	private int row;

	@Key("@col")
	private int column;

	@Key("@inputValue")
	private String value;

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

	public static Cell createForUpdating(int row, int column, String value) {
		Cell cell = new Cell();

		cell.row = row;
		cell.column = column;
		cell.value = value;

		return cell;
	}
}
