package app.android.gambit.remote;


import com.google.api.client.util.Key;


class CellEntry
{
	@Key("gs:cell")
	private Cell cell;

	@Key
	private String id;

	@Key
	private String content;

	public Cell getCell() {
		return cell;
	}

	public String getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public boolean isEmpty() {
		return cell.isEmpty() && id.isEmpty() && content.isEmpty();
	}

	public CellEntry(WorksheetEntry worksheet, Cell cell) {
		this();

		this.cell = cell;
		id = worksheet.getCellEditUrl(cell.getRow(), cell.getColumn()).toString();
	}

	public CellEntry() {
		cell = new Cell(); // empty cell
		id = new String();
		content = new String();
	}
}
