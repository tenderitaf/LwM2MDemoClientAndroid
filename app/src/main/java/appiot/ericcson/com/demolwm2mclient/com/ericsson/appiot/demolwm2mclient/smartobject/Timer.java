package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.response.ReadResponse;

import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Created by JoHe1056 on 2017-02-20.
 */

public class Timer  extends SmartObject  {

    private int interval;
    private java.util.Timer timer;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public Timer() {
        super(0);
        timer = new java.util.Timer();
    }

    @Override
    public ReadResponse read(int resourceid) {
        switch(resourceid) {
            case 5521:
                return ReadResponse.success(resourceid, (double)System.currentTimeMillis() / 1000);
            default:
                return super.read(resourceid);
        }
    }

    @Override
    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Timer.this.fireResourcesChange(5521);
            }
        }, 1000, 1000);
    }

    @Override
    public void stop() {
        if(timer != null) {
            timer.cancel();
        }
    }

    @Override
    public int getObjectId() {
        return 3340;
    }
}
