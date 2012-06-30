package app.android.gambit.remote;


import android.text.TextUtils;
import com.google.api.client.util.Key;


public class CellEntry
{
	@Key("gs:cell")
	private Cell cell;

	@Key
	private String id;

	@Key
	private String content;

	public CellEntry() {
		cell = new Cell();
		id = new String();
		content = new String();
	}

	public CellEntry(WorksheetEntry worksheet, Cell cell) {
		this();

		this.cell = cell;
		id = worksheet.getCellEditUrl(cell.getRow(), cell.getColumn()).toString();
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

	public boolean isEmpty() {
		return cell.isEmpty() && TextUtils.isEmpty(id) && TextUtils.isEmpty(content);
	}
}
