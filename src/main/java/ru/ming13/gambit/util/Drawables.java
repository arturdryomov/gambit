package ru.ming13.gambit.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public final class Drawables
{
	private final Resources resources;

	public static Drawables of(@NonNull Context context) {
		return new Drawables(context);
	}

	private Drawables(Context context) {
		this.resources = context.getResources();
	}

	public Drawable getNormal(@DrawableRes int drawableId) {
		return resources.getDrawable(drawableId).mutate();
	}

	public Drawable getTinted(@DrawableRes int drawableId, @ColorRes int colorId) {
		Drawable drawable = resources.getDrawable(drawableId).mutate();
		int color = resources.getColor(colorId);

		drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

		return drawable;
	}
}
