package ru.ming13.gambit.ui.util;


import java.util.Collection;

import android.os.Build;
import android.widget.ArrayAdapter;


public final class AdapterFiller
{
	private AdapterFiller() {
	}

	public static <T> void fill(ArrayAdapter<T> adapter, Collection<T> collection) {
		if (isSystemArrayAdapterBatchFillingAvailable()) {
			adapter.addAll(collection);
		}
		else {
			batchFillAdapter(adapter, collection);
		}
	}

	private static boolean isSystemArrayAdapterBatchFillingAvailable() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static <T> void batchFillAdapter(ArrayAdapter<T> adapter, Collection<T> collection) {
		adapter.setNotifyOnChange(false);

		for (T item : collection) {
			adapter.add(item);
		}

		adapter.notifyDataSetChanged();
	}
}
