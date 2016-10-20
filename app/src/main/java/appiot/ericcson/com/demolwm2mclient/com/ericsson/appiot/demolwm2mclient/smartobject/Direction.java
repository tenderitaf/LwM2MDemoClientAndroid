package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.leshan.core.response.ReadResponse;


public class Direction extends GenericSensor implements SensorEventListener {
	public static final String TAG = "Direction";

	public static final int ID = 3332;
    private String units = "deg";
	private TextView txtDirection;
	private SensorManager sensorManager;
	private float latestValue = 0.0f;
	private Handler handler;
	private long latestMeasurement = 0;

	public Direction(int instanceId, SensorManager sensorManager, TextView txtDirection) {
		super(instanceId);
		super.setUnits(units);
		this.sensorManager = sensorManager;
		this.txtDirection = txtDirection;
		handler = new Handler();
    }


	@Override
	public void start() {
		Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if(orientationSensor != null) {
			sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
		} else {
			Log.d(TAG, "No direction sensor found.");
		}

	}

	@Override
	public void stop() {
		sensorManager.unregisterListener(this);
	}

	@Override
	public int getObjectId() {
		return ID;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(System.currentTimeMillis() - latestMeasurement > 1000) {

			final float value = (float) Math.floor(event.values[0]);
			if (Math.abs(latestValue - value) >= 1.0f) {
				Log.d(TAG, "Direction value: " + value);
				setValue(5705, value);
				latestValue = value;
				handler.post(new Runnable() {
					public void run() {
						txtDirection.setText(Float.toString(value));
					}
				});
			}
			latestMeasurement = System.currentTimeMillis();
		}
	}

	@Override
	public ReadResponse read(int resourceid) {
		switch (resourceid) {
			case 5705:
				return ReadResponse.success(resourceid, getValue());
			default:
				return super.read(resourceid);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
        

}