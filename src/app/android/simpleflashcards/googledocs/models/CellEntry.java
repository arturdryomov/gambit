package app.android.simpleflashcards.googledocs.models;


import com.google.api.client.util.Key;

public class CellEntry
{
	@Key("gs:cell")
	private Cell cell;

	@Key
	private String id;

	@Key
	private String content;

	public static CellEntry createForUpdating(WorksheetEntry worksheet, Cell cell) {
		CellEntry entry = new CellEntry();
		entry.cell = cell;
		entry.id = worksheet.getCellEditUrl(cell.getRow(), cell.getColumn()).toString();

		return entry;
	}

	public Cell getCell() {
		return cell;
	}

	public String getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}
