package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import android.graphics.Color;
import android.os.Handler;
import android.widget.TextView;

import java.util.logging.Logger;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

public class AddressableTextDisplay extends SmartObject {
	private final Logger logger = Logger.getLogger(this.getClass().getName()); 
    private String text;
    private TextView txtDisplay;
    private Handler handler;

    public AddressableTextDisplay(int instanceId, TextView txtDisplay) {
        super(instanceId);
        this.txtDisplay = txtDisplay;
        handler = new Handler();
    }

    @Override
	public WriteResponse write(int resourceid, LwM2mResource value) {
        switch (resourceid) {
            case 5527:
                setText(value.getValue().toString());
                return WriteResponse.success();
            default:
                return WriteResponse.notFound();
        }
	}

	@Override
    public ExecuteResponse execute(int resourceid, String params) {
        switch (resourceid) {
        case 5530:
        	setText("");
        	return ExecuteResponse.success();
        default: 
        	return super.execute(resourceid, params);
        }
    }

	@Override
    public ReadResponse read(int resourceid) {
        logger.finest("Read on Text Resource " + resourceid);
        switch (resourceid) {
        case 5527:
            return ReadResponse.success(resourceid, getText());
        default:
            return super.read(resourceid);
        }
    }

    public String getText() {
		return text;
	}

	public void setText(final String value) {
        handler.post(new Runnable() {
            public void run() {
                try {
                    txtDisplay.setText(value);
                } catch (Exception e) {}
            }
        });
        this.text = value;
		fireResourcesChange(5527);
	}

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public int getObjectId() {
        return 3341;
    }
}