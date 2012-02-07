package app.android.simpleflashcards.spreadsheets.models;


import com.google.api.client.util.Key;

public class CellEntry
{
	@Key("gs:cell")
	public Cell cell;

	@Key
	public String id;

	@Key
	public String content;

	public static CellEntry createForUpdating(WorksheetEntry worksheet, Cell cell) {
		CellEntry entry = new CellEntry();
		entry.cell = cell;
		entry.id = worksheet.getCellEditUrl(cell.row, cell.column).toString();

		return entry;
	}
}
