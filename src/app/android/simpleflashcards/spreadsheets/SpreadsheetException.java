package app.android.simpleflashcards.spreadsheets;


public class SpreadsheetException extends RuntimeException
{
	public SpreadsheetException() {
	}

	public SpreadsheetException(String message) {
		super(message);
	}

	public SpreadsheetException(Throwable cause) {
		super(cause);
	}

	public SpreadsheetException(String message, Throwable cause) {
		super(message, cause);
	}
}
