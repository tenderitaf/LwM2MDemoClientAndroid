package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;


import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.util.Hex;


public class Location extends SmartObject implements LocationListener {

	private static final String TAG = "SO Location";
	public static int ID = 6;
	private final Handler handler;

	private float latitude = 0.0f;
	private float longitude = 0.0f;
	private float altitude = 0.0f;
	private float uncertainty = 0.0f;
	private float velocity = 0f;
	private Date timestamp = new Date();
	private DateFormat df;


	private TextView txtLatitude;
	private TextView txtLongitude;
	private TextView txtAltitude;
	private TextView txtUncertainty;


	private LocationManager locationManager;

	@Override
	public void start() {

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
	}

	@Override
	public void stop() {
		locationManager.removeUpdates(this);
	}

	@Override
	public int getObjectId() {
		return ID;
	}

	public Location(int instanceId, LocationManager locationManager, TextView txtLatitude, TextView txtLongitude, TextView txtAltitude, TextView txtUncertainty) {
        super(instanceId);
		this.locationManager = locationManager;
		this.txtLatitude = txtLatitude;
		this.txtLongitude = txtLongitude;
		this.txtAltitude = txtAltitude;
		this.txtUncertainty = txtUncertainty;
		handler = new Handler();
		timestamp = new Date();
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
    }

	@Override
	public void onLocationChanged(android.location.Location location) {
		Log.i(TAG, "new Reading, lon:" + location.getLongitude() + " lat:" + location.getLatitude());
		List<Integer> resourcesChanged = new Vector<Integer>();

		if(location.getLatitude() != latitude) {
			latitude = (float)location.getLatitude();
			handler.post(new Runnable() {
				public void run() {
					txtLatitude.setText(Float.toString(latitude));
				}
			});
			resourcesChanged.add(0);
		}

		if(location.getLongitude() != longitude) {
			longitude = (float)location.getLongitude();
			handler.post(new Runnable() {
				public void run() {
					txtLongitude.setText(Float.toString(longitude));	}});
			resourcesChanged.add(1);
		}

		if(location.getAltitude() != altitude) {
			altitude = (float) location.getAltitude();
			handler.post(new Runnable() {
				public void run() {
					txtAltitude.setText(Float.toString(altitude));
				}
			});
			resourcesChanged.add(2);
		}


		if(location.getAccuracy() != uncertainty) {
			uncertainty = location.getAccuracy();
			handler.post(new Runnable() {
				public void run() {
					txtUncertainty.setText(Float.toString(uncertainty));
				}
			});
			resourcesChanged.add(3);
		}

		if(location.getSpeed() != velocity) {
			velocity = location.getSpeed();
			resourcesChanged.add(4);
		}

		if(location.getTime() != timestamp.getTime()) {
			timestamp = new Date(location.getTime());
			resourcesChanged.add(5);
		}


		if(resourcesChanged.size() > 0) {
			int[] resources = new int[resourcesChanged.size()];
			int index = 0;
			for(Integer r : resourcesChanged) {
				resources[index] = r;
				index++;
			}
			fireResourcesChange(resources);


		} else {
			Log.v(TAG,  "Nothing to fire!");
		}


	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}


    @Override
    public ReadResponse read(int resourceid) {

        switch (resourceid) {
        case 0:
            return ReadResponse.success(resourceid, getLatitude());
        case 1:
            return ReadResponse.success(resourceid, getLongitude());
        case 2:
            return ReadResponse.success(resourceid, getAltitude());
        case 3:
            return ReadResponse.success(resourceid, getUncertainty());
        case 4:
            return ReadResponse.success(resourceid, Hex.encodeHexString(getVelocity().getBytes()));
        case 5:
            return ReadResponse.success(resourceid, getTimestamp());
        default:
            return super.read(resourceid);
        }
    }

    public String getLatitude() {
        return Float.toString(latitude);
    }

    public String getLongitude() {
        return Float.toString(longitude);
    }

    public String getAltitude() {
    	return Float.toString(altitude);
    }
    
    public String getUncertainty() {
    	return Float.toString(uncertainty);
    }
    
    public String getVelocity() { return Float.toString(velocity); }
    
    public Date getTimestamp() {
        return timestamp;
    }
}