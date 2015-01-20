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

	@IdRes
	private final int animatorId;

	public static ViewDirector of(@NonNull Activity activity, @IdRes int animatorId) {
		return new ViewDirector(activity, animatorId);
	}

	public static ViewDirector of(@NonNull Fragment fragment, @IdRes int animatorId) {
		return new ViewDirector(fragment, animatorId);
	}

	private ViewDirector(Activity activity, int animatorId) {
		this.activity = activity;
		this.fragment = null;

		this.animatorId = animatorId;
	}

	private ViewDirector(Fragment fragment, int animatorId) {
		this.activity = null;
		this.fragment = fragment;

		this.animatorId = animatorId;
	}

	public void show(@IdRes int viewId) {
		ViewAnimator animator = (ViewAnimator) findView(animatorId);
		View view = findView(viewId);

		if (animator.getDisplayedChild() != animator.indexOfChild(view)) {
			animator.setDisplayedChild(animator.indexOfChild(view));
		}
	}

	private View findView(int viewId) {
		if (activity != null) {
			return ButterKnife.findById(activity, viewId);
		} else {
			return ButterKnife.findById(fragment.getView(), viewId);
		}
	}
}