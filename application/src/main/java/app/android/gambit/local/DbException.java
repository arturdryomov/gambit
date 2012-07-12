package app.android.gambit.local;


class DbException extends RuntimeException
{
	public DbException() {
	}

	public DbException(String detailMessage) {
		super(detailMessage);
	}
}
