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
