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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.net.Uri;


public class DeckCardsOrderShufflingTask extends DeckCardsOrderChangingTask
{
	public static void execute(ContentResolver contentResolver, Uri cardsUri) {
		new DeckCardsOrderShufflingTask(contentResolver, cardsUri).execute();
	}

	private DeckCardsOrderShufflingTask(ContentResolver contentResolver, Uri cardsUri) {
		super(contentResolver, cardsUri);
	}

	@Override
	protected List<Integer> buildCardsOrderIndices(int indicesCount) {
		return generateShuffledNaturalNumbers(indicesCount);
	}

	private List<Integer> generateShuffledNaturalNumbers(int numbersCount) {
		List<Integer> naturalNumbersList = generateNaturalNumbers(numbersCount);

		Collections.shuffle(naturalNumbersList);

		return naturalNumbersList;
	}

	private List<Integer> generateNaturalNumbers(int numbersCount) {
		List<Integer> naturalNumbersList = new ArrayList<Integer>();

		for (Integer naturalNumber = 0; naturalNumber < numbersCount; naturalNumber++) {
			naturalNumbersList.add(naturalNumber);
		}

		return naturalNumbersList;
	}
}
