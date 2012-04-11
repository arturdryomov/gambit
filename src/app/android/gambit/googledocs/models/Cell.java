package app.android.gambit.googledocs.models;


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

	public boolean isEmpty() {
		return value.isEmpty();
	}

	public Cell(int row, int column, String value) {
		this.row = row;
		this.column = column;
		this.value = value;
	}

	public Cell() {
		value = new String();
	}
}
