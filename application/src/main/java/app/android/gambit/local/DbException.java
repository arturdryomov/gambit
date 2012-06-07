package app.android.gambit.local;


public class DbException extends RuntimeException
{
	public DbException() {
	}

	public DbException(String detailMessage) {
		super(detailMessage);
	}
}
