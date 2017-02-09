package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;


public class Colour extends GenericSensor {
	public static final String TAG = "Colour";

	public static final int ID = 3335;
    private String units = "RGB";
	private TextView txtDirection;
	private String color = "#cccccc";
	private Handler handler;
	LinearLayout layout;

    public Colour(int instanceId, LinearLayout layout) {
		super(instanceId);
		super.setUnits(units);
		this.layout = layout;
		handler = new Handler();
    }


	@Override
	public void start() {
		handler.post(new Runnable() {
			public void run() {
				try {
					layout.setBackgroundColor(Color.parseColor(color));
				} catch (Exception e) {}
			}
		});
	}

	@Override
	public void stop() {

	}

	public String getColor() {
		return color;
	}

	@Override
	public int getObjectId() {
		return ID;
	}

	@Override
	public ReadResponse read(int resourceid) {
		switch (resourceid) {
			case 5701:
				return ReadResponse.success(resourceid, "RGB");
			case 5706:
				return ReadResponse.success(resourceid, color);
			case 5750:
				return ReadResponse.success(resourceid, "");
			default:
				return super.read(resourceid);
		}
	}

	@Override
	public WriteResponse write(int resourceid, LwM2mResource value) {
		if(resourceid == 5706) {
			this.color = value.getValue().toString();
			try {
				final int colorValue = Color.parseColor(color);
				handler.post(new Runnable() {
					public void run() {
						try {
							layout.setBackgroundColor(colorValue);
						} catch (Exception e) {}
					}
				});
			} catch(IllegalArgumentException e) {
				return WriteResponse.badRequest(this.color + " is not a supported value.");
			}
		}
		return WriteResponse.success();

	}

}