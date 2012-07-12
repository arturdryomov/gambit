package app.android.gambit.remote;


public class RemoteCard
{
	private String frontSideText;
	private String backSideText;

	public RemoteCard() {
		frontSideText = new String();
		backSideText = new String();
	}

	public RemoteCard(String frontSideText, String backSideText) {
		this.frontSideText = frontSideText;
		this.backSideText = backSideText;
	}

	public String getFrontSideText() {
		return frontSideText;
	}

	public void setFrontSideText(String text) {
		frontSideText = text;
	}

	public String getBackSideText() {
		return backSideText;
	}

	public void setBackSideText(String text) {
		backSideText = text;
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object otherObject) {
		if (this == otherObject) {
			return true;
		}

		if (!(otherObject instanceof RemoteCard)) {
			return false;
		}

		RemoteCard otherCard = (RemoteCard) otherObject;

		if ((frontSideText == null) && (otherCard.frontSideText != null)) {
			return false;
		}

		if ((frontSideText != null) && !frontSideText.equals(otherCard.frontSideText)) {
			return false;
		}

		if ((backSideText == null) && (otherCard.backSideText != null)) {
			return false;
		}

		if ((backSideText != null) && !backSideText.equals(otherCard.backSideText)) {
			return false;
		}

		return true;
	}
}
