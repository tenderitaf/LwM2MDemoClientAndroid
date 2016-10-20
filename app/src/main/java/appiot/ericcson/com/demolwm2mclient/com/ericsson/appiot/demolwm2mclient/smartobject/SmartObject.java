package appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import appiot.ericcson.com.demolwm2mclient.Resource;

/**
 * Created by JoHe1056 on 2016-10-12.
 */
public abstract class SmartObject extends BaseInstanceEnabler
{

    private Hashtable<Integer, Resource> resources = new Hashtable<Integer, Resource>();

    private int instanceId = 0;

    public abstract void start();
    public abstract void stop();


    public abstract int getObjectId();

    public SmartObject(int instanceId) {
        this.instanceId = instanceId;
    }

    public void addResource(Resource resource) {
        resources.put(resource.getId(), resource);
    }

    public Resource getResource(Integer id) {
        return resources.get(id);
    }

    public BaseInstanceEnabler getInstanceEnabler() {
        return (BaseInstanceEnabler) this;
    }


    public void fireResourcesChange(List<Integer> resourcesChanged) {
        if (resourcesChanged.size() > 0) {
            int[] resources = new int[resourcesChanged.size()];
            int index = 0;
            for (Integer r : resourcesChanged) {
                resources[index] = r;
                index++;
            }
            fireResourcesChange(resources);
        }
    }
}
