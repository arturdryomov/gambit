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

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ViewAnimator;

import butterknife.ButterKnife;

public final class ViewDirector
{
	private final Activity activity;
	private final Fragment fragment;

	private final int animatorId;

	public static ViewDirector of(@NonNull Activity activity, @IdRes int animatorId) {
		return new ViewDirector(activity, null, animatorId);
	}

	public static ViewDirector of(@NonNull Fragment fragment, @IdRes int animatorId) {
		return new ViewDirector(null, fragment, animatorId);
	}

	private ViewDirector(Activity activity, Fragment fragment, int animatorId) {
		this.activity = activity;
		this.fragment = fragment;

		this.animatorId = animatorId;
	}

	public void show(@IdRes int viewId) {
		ViewAnimator animator = findView(animatorId);
		View view = findView(viewId);

		if (animator.getDisplayedChild() != animator.indexOfChild(view)) {
			animator.setDisplayedChild(animator.indexOfChild(view));
		}
	}

	private <T extends View> T findView(int viewId) {
		if (activity != null) {
			return ButterKnife.findById(activity, viewId);
		} else {
			return ButterKnife.findById(fragment.getView(), viewId);
		}
	}
}