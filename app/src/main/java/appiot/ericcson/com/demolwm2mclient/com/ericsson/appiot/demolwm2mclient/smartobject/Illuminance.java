package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class Illuminance extends GenericSensor implements SensorEventListener {
	public static final String TAG = "Illuminance";
	public static final int ID = 3301;
	private SensorManager sensorManager;
	private TextView txtIlluminance;
	private Handler handler;
	private long latestMeasurement = 0;
    public Illuminance(int instanceId, SensorManager sensorManager, TextView txtIlluminance) {
		super(instanceId);
		this.txtIlluminance = txtIlluminance;
		this.sensorManager = sensorManager;
		super.setUnits("lx");
		handler = new Handler();
    }

	@Override
	public void start() {
		Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if(lightSensor != null) {
			sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
		} else {
			Log.d(TAG, "No light sensor found.");
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
	public void onSensorChanged(final SensorEvent event) {
		if(System.currentTimeMillis() - latestMeasurement > 1000) {
			Log.v(TAG, "Light value: " + event.values[0]);
			setValue(event.values[0]);
			handler.post(new Runnable() {
				public void run() {
					txtIlluminance.setText(Float.toString(event.values[0]));
				}
			});
			latestMeasurement = System.currentTimeMillis();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}