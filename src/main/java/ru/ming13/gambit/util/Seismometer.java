package ru.ming13.gambit.util;

import android.content.Context;
import android.hardware.SensorManager;

import com.squareup.seismic.ShakeDetector;

import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeviceShakedEvent;

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
		BusProvider.getBus().post(new DeviceShakedEvent());
	}

	public void enable() {
		shakeDetector.start(sensorManager);
	}

	public void disable() {
		shakeDetector.stop();
	}
}
