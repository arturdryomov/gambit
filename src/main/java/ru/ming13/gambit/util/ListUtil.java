package ru.ming13.gambit.util;

import android.widget.ListView;

public final class ListUtil implements Runnable
{
	private final ListView list;

	public static ListUtil at(ListView list) {
		return new ListUtil(list);
	}

	private ListUtil(ListView list) {
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
