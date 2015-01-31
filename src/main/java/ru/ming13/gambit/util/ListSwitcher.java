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

package ru.ming13.gambit.util;

import android.support.annotation.NonNull;
import android.widget.ListView;

public final class ListSwitcher implements Runnable
{
	private final ListView list;

	public static ListSwitcher at(@NonNull ListView list) {
		return new ListSwitcher(list);
	}

	private ListSwitcher(ListView list) {
		this.list = list;
	}

	public void switchChoiceModeToMultipleModal() {
		// Remove checked items on switching modes.
		// http://stackoverflow.com/a/14633482/3359826

		list.clearChoices();

		list.post(this);
	}

	@Override
	public void run() {
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	}
}
