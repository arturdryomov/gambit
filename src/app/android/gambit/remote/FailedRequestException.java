package app.android.gambit.remote;


public class FailedRequestException extends SyncException
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
