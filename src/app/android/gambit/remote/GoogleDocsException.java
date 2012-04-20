package app.android.gambit.remote;

public class GoogleDocsException extends RuntimeException
{
	public GoogleDocsException() {
	}

	public GoogleDocsException(String message) {
		super(message);
	}

	public GoogleDocsException(Throwable cause) {
		super(cause);
	}

	public GoogleDocsException(String message, Throwable cause) {
		super(message, cause);
	}
}
