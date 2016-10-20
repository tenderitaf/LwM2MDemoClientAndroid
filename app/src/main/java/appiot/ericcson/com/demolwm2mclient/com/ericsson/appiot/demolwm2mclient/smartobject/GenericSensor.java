package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.SmartObject;


public abstract class GenericSensor extends SmartObject {

	public static final int ID = 3300;

    private String units = "Cel";

	private float value;
	private Date timestamp;
	private float minMeasuredValue = 0.0f;
	private float maxMeasuredValue = 0.0f;
	private float maxRangeValue;
	private float minRangeValue;

	public GenericSensor(int instanceId) {
		super(instanceId);
    }

	@Override
	public ExecuteResponse execute(int resourceid, String params) {
		switch (resourceid) {
			case 5605:
				timestamp = new Date();
				setMaxMeasuredValue(0.0f);
				setMinMeasuredValue(0.0f);
				fireResourcesChange(0, 5601, 5602);
				return ExecuteResponse.success();
			default:
				return super.execute(resourceid, params);
		}
	}

	@Override
	public ReadResponse read(int resourceid) {
		switch (resourceid) {
			case 5700:
				return ReadResponse.success(resourceid, getValue());
			case 5701:
				return ReadResponse.success(resourceid, getUnits());
			case 5601:
				return ReadResponse.success(resourceid, getMinMeasuredValue());
			case 5602:
				return ReadResponse.success(resourceid, getMaxMeasuredValue());
			case 5603:
				return ReadResponse.success(resourceid, getMinRangeValue());
			case 5604:
				return ReadResponse.success(resourceid, getMaxRangeValue());
			default:
				return super.read(resourceid);
		}
	}


	public void setValue(float newValue) {
		setValue(5700, newValue);
	}

	public void setValue(int resourceId, float newValue) {

		value = newValue;
		timestamp = new Date();
		List<Integer> resourcesChanged = new Vector<Integer>();
		resourcesChanged.add(resourceId);


		if(value > maxMeasuredValue) {
			maxMeasuredValue = value;
			resourcesChanged.add(5602);
		}
		else if(value < minMeasuredValue) {
			minMeasuredValue = value;
			resourcesChanged.add(5601);
		}

		fireResourcesChange(resourcesChanged);
	}
    
    public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public float getMinMeasuredValue() {
		return minMeasuredValue;
	}

	public void setMinMeasuredValue(float minMeasuredValue) {
		this.minMeasuredValue = minMeasuredValue;
	}

	public float getMaxMeasuredValue() {
		return maxMeasuredValue;
	}

	public void setMaxMeasuredValue(float maxMeasuredValue) {
		this.maxMeasuredValue = maxMeasuredValue;
	}

	public float getMinRangeValue() {
		return minRangeValue;
	}

	public void setMinRangeValue(float minRangeValue) {
		this.minRangeValue = minRangeValue;
	}

	public float getMaxRangeValue() {
		return maxRangeValue;
	}

	public void setMaxRangeValue(float maxRangeValue) {
		this.maxRangeValue = maxRangeValue;
	}

	public float getValue() {
        return value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

	@Override
	public int getObjectId() {
		return ID;
	}

}