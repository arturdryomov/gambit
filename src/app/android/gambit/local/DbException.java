package app.android.gambit.local;


public class DbException extends RuntimeException
{
	public DbException() {
	}

	public DbException(String detailMessage) {
		super(detailMessage);
	}

	public DbException(Throwable throwable) {
		super(throwable);
	}

	public DbException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
