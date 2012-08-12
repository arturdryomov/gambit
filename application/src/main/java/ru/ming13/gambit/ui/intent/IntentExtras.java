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

package ru.ming13.gambit.ui.intent;


public final class IntentExtras
{
	private IntentExtras() {
	}

	public static final String DECK;
	public static final String CARD;

	private static final String EXTRA_PREFIX = IntentFactory.class.getPackage().getName();

	private static final String DECK_POSTFIX = "deck";
	private static final String CARD_POSTFIX = "card";

	static {
		DECK = String.format("%s.%s", EXTRA_PREFIX, DECK_POSTFIX);
		CARD = String.format("%s.%s", EXTRA_PREFIX, CARD_POSTFIX);
	}
}
