package app.android.simpleflashcards.spreadsheets;


public class FailedRequestException extends SpreadsheetException
{
	public FailedRequestException() {
	}

	public FailedRequestException(String message) {
		super(message);
	}

	public FailedRequestException(Throwable cause) {
		super(cause);
	}

	public FailedRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
