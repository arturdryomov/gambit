package ru.ming13.gambit.ui.util;


import java.util.Collection;

import android.os.Build;
import android.widget.ArrayAdapter;


public final class AdapterFiller
{
	private AdapterFiller() {
	}

	public static <T> void fill(ArrayAdapter<T> adapter, Collection<T> collection) {
		if (isSystemArrayAdapterAddAllMethodAvailable()) {
			adapter.addAll(collection);
		}
		else {
			addAll(adapter, collection);
		}
	}

	private static boolean isSystemArrayAdapterAddAllMethodAvailable() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static <T> void addAll(ArrayAdapter<T> adapter, Collection<T> collection) {
		adapter.setNotifyOnChange(false);

		for (T item : collection) {
			adapter.add(item);
		}

		adapter.notifyDataSetChanged();
	}
}
