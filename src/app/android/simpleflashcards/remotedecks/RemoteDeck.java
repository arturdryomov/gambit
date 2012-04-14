package app.android.simpleflashcards.remotedecks;


import java.util.List;


public class RemoteDeck
{
	private String title;
	private List<RemoteCard> cards;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<RemoteCard> getCards() {
		return cards;
	}

	public void setCards(List<RemoteCard> cardsList) {
		cards = cardsList;
	}

	@Override
	public int hashCode() {
		// hashCode() is not intended to be used
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object otherObject) {
		if (this == otherObject) {
			return true;
		}

		if (!(otherObject instanceof RemoteDeck)) {
			return false;
		}

		RemoteDeck otherDeck = (RemoteDeck) otherObject;

		if ((title == null) && (otherDeck.title != null)) {
			return false;
		}

		if ((title != null) && !title.equals(otherDeck.title)) {
			return false;
		}

		if (!cards.equals(otherDeck.cards)) {
			return false;
		}

		return true;
	}

}
