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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

public final class Animations
{
	private Animations() {
	}

	public static void shake(@NonNull View view) {
		Animator shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
		shakeAnimator.setDuration(700);

		shakeAnimator.start();
	}
}
