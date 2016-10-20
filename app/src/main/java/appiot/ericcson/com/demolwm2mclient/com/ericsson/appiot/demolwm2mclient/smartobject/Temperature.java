package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;



public class Temperature extends GenericSensor implements SensorEventListener {
	public static final String TAG = "Temperature";

	public static final int ID = 3303;
    private String units = "Cel";

    
    
    public Temperature(int instanceId) {
		super(instanceId);
		super.setUnits(units);
    }


	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public int getObjectId() {
		return ID;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.d(TAG, "Temperature value: " + event.values[0]);
		setValue(event.values[0]);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
        

}