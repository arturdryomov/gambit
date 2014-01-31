package ru.ming13.gambit.util;

import android.app.ActionBar;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.OperationCancelledEvent;
import ru.ming13.gambit.bus.OperationSavedEvent;

public final class OperationBar implements View.OnClickListener
{
	private final ActionBar actionBar;

	public static OperationBar at(Activity activity) {
		return new OperationBar(activity);
	}

	private OperationBar(Activity activity) {
		this.actionBar = activity.getActionBar();
	}

	public void show() {
		setUpBarView();
		setUpBarListener();
	}

	private void setUpBarView() {
		actionBar.setCustomView(buildBarView(), buildBarParams());
	}

	private View buildBarView() {
		LayoutInflater layoutInflater = LayoutInflater.from(actionBar.getThemedContext());

		return layoutInflater.inflate(R.layout.bar_operation, null);
	}

	private ActionBar.LayoutParams buildBarParams() {
		return new ActionBar.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	private void setUpBarListener() {
		actionBar.getCustomView().findViewById(R.id.button_cancel).setOnClickListener(this);
		actionBar.getCustomView().findViewById(R.id.button_save).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_cancel:
				BusProvider.getBus().post(new OperationCancelledEvent());
				break;

			case R.id.button_save:
				BusProvider.getBus().post(new OperationSavedEvent());
				break;

			default:
				break;
		}
	}
}
