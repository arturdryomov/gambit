/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.android.gambit.remote;


import java.util.ArrayList;
import java.util.List;


public class RemoteDeck
{
	private String title;
	private List<RemoteCard> cards;

	public RemoteDeck() {
		title = new String();
		cards = new ArrayList<RemoteCard>();
	}

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
