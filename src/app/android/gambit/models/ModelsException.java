package app.android.gambit.models;


public class ModelsException extends RuntimeException
{
	public ModelsException() {
	}

	public ModelsException(String detailMessage) {
		super(detailMessage);
	}

	public ModelsException(Throwable throwable) {
		super(throwable);
	}

	public ModelsException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
