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

package ru.ming13.gambit.provider;

final class GambitPathsBuilder
{
	private static final class Segments
	{
		private Segments() {
		}

		public static final String DECKS = "decks";
		public static final String CARDS = "cards";
	}

	public String buildDecksPath() {
		return Segments.DECKS;
	}

	public String buildDeckPath(String deckNumber) {
		return String.format("%s/%s", Segments.DECKS, deckNumber);
	}

	public String buildCardsPath(String deckNumber) {
		return String.format("%s/%s/%s", Segments.DECKS, deckNumber, Segments.CARDS);
	}

	public String buildCardPath(String deckNumber, String cardNumber) {
		return String.format("%s/%s/%s/%s", Segments.DECKS, deckNumber, Segments.CARDS, cardNumber);
	}
}
