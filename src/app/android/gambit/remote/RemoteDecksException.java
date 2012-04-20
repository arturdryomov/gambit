package app.android.gambit.remote;


public class RemoteDecksException extends RuntimeException
{
	public RemoteDecksException() {
	}

	public RemoteDecksException(String detailMessage) {
		super(detailMessage);
	}

	public RemoteDecksException(Throwable throwable) {
		super(throwable);
	}

	public RemoteDecksException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
