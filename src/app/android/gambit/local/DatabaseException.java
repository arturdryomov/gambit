package app.android.gambit.local;


public class DatabaseException extends RuntimeException
{
	public DatabaseException() {
	}

	public DatabaseException(String detailMessage) {
		super(detailMessage);
	}

	public DatabaseException(Throwable throwable) {
		super(throwable);
	}

	public DatabaseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
