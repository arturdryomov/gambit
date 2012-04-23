package app.android.gambit.remote;

public class SyncException extends RuntimeException
{
	public SyncException() {
	}

	public SyncException(String message) {
		super(message);
	}

	public SyncException(Throwable cause) {
		super(cause);
	}

	public SyncException(String message, Throwable cause) {
		super(message, cause);
	}
}
