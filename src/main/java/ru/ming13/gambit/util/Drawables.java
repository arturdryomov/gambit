package ru.ming13.gambit.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public final class Drawables
{
	private final Context context;

	public static Drawables of(@NonNull Context context) {
		return new Drawables(context);
	}

	private Drawables(Context context) {
		this.context = context.getApplicationContext();
	}

	public Drawable getNormal(@DrawableRes int drawableId) {
		return context.getResources().getDrawable(drawableId).mutate();
	}

	public Drawable getTinted(@DrawableRes int drawableId, @ColorRes int colorId) {
		Drawable drawable = context.getResources().getDrawable(drawableId).mutate();
		int color = context.getResources().getColor(colorId);

		drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

		return drawable;
	}
}
