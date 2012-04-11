package app.android.gambit.ui;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class ShakeListener implements SensorEventListener
{
	private static final int MINIMUM_FORCE = 10;
	private static final int MINIMUM_DIRECTION_CHANGE_COUNT = 3;
	private static final int MAXIMUM_PAUSE_BETWEEN_DIRECTION_CHANGES = 200;
	private static final int MAXIMUM_DURATION_OF_SHAKE = 400;

	private long firstDirectionChangeTime = 0;
	private long lastDirectionChangeTime;
	private int directionChangesCount = 0;

	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;

	private OnShakeListener shakeListener;

	public interface OnShakeListener
	{
		void onShake();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		shakeListener = listener;
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		float x = sensorEvent.values[SensorManager.DATA_X];
		float y = sensorEvent.values[SensorManager.DATA_Y];
		float z = sensorEvent.values[SensorManager.DATA_Z];

		float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

		if (totalMovement > MINIMUM_FORCE) {
			long currentTime = System.currentTimeMillis();

			if (firstDirectionChangeTime == 0) {
				firstDirectionChangeTime = currentTime;
				lastDirectionChangeTime = currentTime;
			}

			long lastChangePause = currentTime - lastDirectionChangeTime;

			if (lastChangePause < MAXIMUM_PAUSE_BETWEEN_DIRECTION_CHANGES) {
				lastDirectionChangeTime = currentTime;
				directionChangesCount++;

				lastX = x;
				lastY = y;
				lastZ = z;

				if (directionChangesCount >= MINIMUM_DIRECTION_CHANGE_COUNT) {
					long totalDuration = currentTime - firstDirectionChangeTime;

					if (totalDuration < MAXIMUM_DURATION_OF_SHAKE) {
						shakeListener.onShake();
						resetShakeParameters();
					}
				}
			}
			else {
				resetShakeParameters();
			}
		}
	}

	private void resetShakeParameters() {
		firstDirectionChangeTime = 0;
		directionChangesCount = 0;
		lastDirectionChangeTime = 0;

		lastX = 0;
		lastY = 0;
		lastZ = 0;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// Just need to be implemented, but we don’t need it
	}
}