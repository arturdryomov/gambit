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

package ru.ming13.gambit.task;

import android.content.ContentResolver;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import ru.ming13.gambit.provider.GambitContract;

public class DeckCardsOrderResettingTask extends DeckCardsOrderChangingTask
{
	public static void execute(ContentResolver contentResolver, Uri cardsUri) {
		new DeckCardsOrderResettingTask(contentResolver, cardsUri).execute();
	}

	private DeckCardsOrderResettingTask(ContentResolver contentResolver, Uri cardsUri) {
		super(contentResolver, cardsUri);
	}

	@Override
	protected List<Integer> buildCardsOrderIndices(int indicesCount) {
		return generateDefaultCardsIndices(indicesCount);
	}

	private List<Integer> generateDefaultCardsIndices(int indicesCount) {
		List<Integer> cardIndices = new ArrayList<Integer>();

		for (int index = 0; index < indicesCount; index++) {
			cardIndices.add(GambitContract.Cards.Defaults.ORDER_INDEX);
		}

		return cardIndices;
	}
}
