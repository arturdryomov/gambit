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

import android.app.ActionBar;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.OperationCancelledEvent;
import ru.ming13.gambit.bus.OperationSavedEvent;

public final class OperationBar
{
	private final ActionBar actionBar;

	public static OperationBar at(@NonNull Activity activity) {
		return new OperationBar(activity);
	}

	private OperationBar(Activity activity) {
		this.actionBar = activity.getActionBar();
	}

	public void show() {
		setUpBar();
	}

	private void setUpBar() {
		actionBar.setCustomView(buildBarView(), buildBarParams());

		ButterKnife.inject(this, actionBar.getCustomView());
	}

	private View buildBarView() {
		LayoutInflater layoutInflater = LayoutInflater.from(actionBar.getThemedContext());

		return layoutInflater.inflate(R.layout.view_bar_operation, null, false);
	}

	private ActionBar.LayoutParams buildBarParams() {
		return new ActionBar.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@OnClick(R.id.button_cancel)
	public void onCancelButton() {
		BusProvider.getBus().post(new OperationCancelledEvent());
	}

	@OnClick(R.id.button_save)
	public void onSaveButton() {
		BusProvider.getBus().post(new OperationSavedEvent());
	}
}
