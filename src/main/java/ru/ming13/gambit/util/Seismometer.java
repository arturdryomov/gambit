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
import android.hardware.SensorManager;

import com.squareup.seismic.ShakeDetector;

import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeviceShakenEvent;

public final class Seismometer implements ShakeDetector.Listener
{
	private final ShakeDetector shakeDetector;
	private final SensorManager sensorManager;

	public Seismometer(Context context) {
		this.shakeDetector = new ShakeDetector(this);
		this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	@Override
	public void hearShake() {
		BusProvider.getBus().post(new DeviceShakenEvent());
	}

	public void enable() {
		shakeDetector.start(sensorManager);
	}

	public void disable() {
		shakeDetector.stop();
	}
}
