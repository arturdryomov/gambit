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

package ru.ming13.gambit.test.ui.adapter;


import java.util.Random;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.database.Cursor;
import junit.framework.TestCase;
import ru.ming13.gambit.ui.adapter.CardsPagerAdapter;


public class CardsPagerAdapterTests extends TestCase
{
	private CardsPagerAdapter cardsPagerAdapter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		cardsPagerAdapter = new CardsPagerAdapter(null, null);
	}

	public void testAdapterWithNullCursorPagesCount() {
		cardsPagerAdapter.swapCursor(null);

		// Cursor is invalid: nothing at all
		assertThat(cardsPagerAdapter).hasCount(0);
	}

	public void testAdapterWithEmptyCursorPagesCount() {
		Cursor cardsCursor = mockCursor(0);

		cardsPagerAdapter.swapCursor(cardsCursor);

		// Cursor is empty: no cards sign should be shown
		assertThat(cardsPagerAdapter).hasCount(1);
	}

	private Cursor mockCursor(int cursorRowsCount) {
		Cursor cursor = mock(Cursor.class);
		when(cursor.getCount()).thenReturn(cursorRowsCount);

		return cursor;
	}

	public void testAdapterWithNormalCursorPagesCount() {
		int expectedCardsCount  = generatePositiveRandomNumber();

		Cursor cardsCursor = mockCursor(expectedCardsCount);

		cardsPagerAdapter.swapCursor(cardsCursor);

		assertThat(cardsPagerAdapter).hasCount(expectedCardsCount);
	}

	private int generatePositiveRandomNumber() {
		Random random = new Random();

		int highRangeLimit = Integer.MAX_VALUE - 1;

		return random.nextInt(highRangeLimit) + 1;
	}

	public void testAdapterWithNullCursorEmptiness() {
		cardsPagerAdapter.swapCursor(null);

		assertThat(cardsPagerAdapter.isEmpty()).isTrue();
	}

	public void testAdapterWithEmptyCursorEmptiness() {
		Cursor cardsCursor = mockCursor(0);

		cardsPagerAdapter.swapCursor(cardsCursor);

		assertThat(cardsPagerAdapter.isEmpty()).isTrue();
	}

	public void testAdapterWithNormalCursorEmptiness() {
		Cursor cardsCursor = mockCursor(generatePositiveRandomNumber());

		cardsPagerAdapter.swapCursor(cardsCursor);

		assertThat(cardsPagerAdapter.isEmpty()).isFalse();
	}
}
