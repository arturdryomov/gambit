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

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;

public final class Drawables
{
	private final Context context;

	public static Drawables of(@NonNull Context context) {
		return new Drawables(context);
	}

	private Drawables(Context context) {
		this.context = context.getApplicationContext();
	}

	public Drawable getTinted(@DrawableRes int drawableId, @ColorRes int colorId) {
		Drawable drawable = getNormal(drawableId);
		int color = getColor(colorId);

		drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

		return drawable;
	}

	public Drawable getNormal(@DrawableRes int drawableId) {
		return ResourcesCompat.getDrawable(context.getResources(), drawableId, context.getTheme()).mutate();
	}

	private int getColor(@ColorRes int colorId) {
		return context.getResources().getColor(colorId);
	}
}
