package appiot.ericcson.com.demolwm2mclient;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.observer.LwM2mClientObserver;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.client.servers.DmServerInfo;
import org.eclipse.leshan.client.servers.ServerInfo;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;

import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.util.Hex;

import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.AddressableTextDisplay;
import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.Colour;
import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.Device;
import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.Direction;
import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.Illuminance;
import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.Location;
import appiot.ericcson.com.demolwm2mclient.com.ericsson.appiot.demolwm2mclient.smartobject.SmartObject;

import static org.eclipse.leshan.LwM2mId.SECURITY;
import static org.eclipse.leshan.LwM2mId.SERVER;
import static org.eclipse.leshan.client.object.Security.noSecBootstap;
import static org.eclipse.leshan.client.object.Security.pskBootstrap;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String bootstrapUrlUnsecure = "coap://lwm2mdemobs.cloudapp.net:5683";
    private static final String bootstrapUrlSecure = "coaps://lwm2mdemobs.cloudapp.net:5684";

    private LeshanClient client;
    private List<SmartObject> smartObjects = new Vector<SmartObject>();
    public void addSmartObject(SmartObject smartObject) {
        this.smartObjects.add(smartObject);
    }

    LinearLayout layoutBootstrap;
    LinearLayout layoutSecure;
    LinearLayout layoutDeviceManagement;
    LinearLayout layoutColor;
    TextView txtBootstrapUrl;
    TextView txtEndpoint;
    EditText txtIdentity;
    EditText txtPsk;
    TextView txtDmUrl;
    TextView txtIlluminance;
    TextView txtDirection;
    TextView txtLatitude;
    TextView txtLongitude;
    TextView txtAltitude;
    TextView txtUncertainty;
    TextView txtDisplay;

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        handler = new Handler();

        layoutBootstrap = (LinearLayout) findViewById(R.id.layoutBootstrap);
        layoutSecure = (LinearLayout) findViewById(R.id.layoutSecure);
        layoutDeviceManagement = (LinearLayout) findViewById(R.id.layoutDeviceManagement);
        layoutColor = (LinearLayout) findViewById(R.id.layoutColor);
        txtBootstrapUrl = (TextView) findViewById(R.id.txtBootstrapUrl);
        txtEndpoint = (TextView) findViewById(R.id.txtEndpoint);
        txtIdentity = (EditText) findViewById(R.id.txtIdentity);
        txtPsk = (EditText) findViewById(R.id.txtPsk);
        txtDmUrl = (TextView) findViewById(R.id.txtDmUrl);
        txtIlluminance = (TextView) findViewById(R.id.txtIlluminance);
        txtDirection = (TextView) findViewById(R.id.txtDirection);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtAltitude = (TextView) findViewById(R.id.txtAltitude);
        txtUncertainty = (TextView) findViewById(R.id.txtUncertainty);
        txtDisplay = (TextView) findViewById(R.id.txtDisplay);

        setRegistered(false);

        layoutSecure.setVisibility(View.INVISIBLE);

        txtBootstrapUrl.setText(bootstrapUrlUnsecure);
        txtEndpoint.setText(Build.SERIAL);

        final CheckBox useSecureBootstrap = (CheckBox) findViewById(R.id.chkSecure);
        useSecureBootstrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(useSecureBootstrap.isChecked()) {
                    layoutSecure.setVisibility(View.VISIBLE);
                    txtBootstrapUrl.setText(bootstrapUrlSecure);

                } else {
                    layoutSecure.setVisibility(View.INVISIBLE);
                    txtBootstrapUrl.setText(bootstrapUrlUnsecure);
                }
            }
        });


        final Activity mainActivity = this;

        Button button = (Button) findViewById(R.id.btnRegister);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bootstrapUrl = txtBootstrapUrl.getText().toString();
                String endpoint = txtEndpoint.getText().toString();
                String identity = null;
                String psk = null;
                if(useSecureBootstrap.isChecked()) {
                    identity = txtIdentity.getText().toString();
                    psk = txtPsk.getText().toString();
                }
                start(bootstrapUrl, endpoint, identity, psk);
            }
        });

        Button deregisterButton = (Button) findViewById(R.id.btnDeRegister);
        deregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }


    private void setRegistered(final boolean registered) {
        handler.post(new Runnable() {
            public void run() {
                if(registered) {
                    layoutBootstrap.setVisibility(View.GONE);
                    layoutDeviceManagement.setVisibility(View.VISIBLE);
                } else {
                    layoutBootstrap.setVisibility(View.VISIBLE);
                    layoutDeviceManagement.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onPause() {
        super.onPause();
        if(client != null) {
            stop();
        }

    }
        @Override
    public void onResume() {
        super.onResume();
            if(client != null) {
                start(txtBootstrapUrl.getText().toString(), txtEndpoint.getText().toString(), txtIdentity.getText().toString(), txtPsk.getText().toString());
            }
    }

    private void stop() {
        for(SmartObject smartObject : smartObjects) {
            smartObject.stop();
        }
        client.stop(true);
        setRegistered(false);
    }

    private void start(String bootstrapUrl, String endpoint, String identity, String psk) {
        if(client == null) {
            init(bootstrapUrl, endpoint, identity, psk);
        }
        client.start();
        for(SmartObject smartObject : smartObjects) {
            smartObject.start();
        }
    }

    private void init(String bootstrapUrl, String endpoint, String identity, String psk) {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Device device = new Device(0, activityManager);
        addSmartObject(device);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = new Location(0, locationManager, txtLatitude, txtLongitude, txtAltitude, txtUncertainty);
        addSmartObject(location);

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Illuminance illuminance = new Illuminance(0, sensorManager, txtIlluminance);
        addSmartObject(illuminance);

        Direction direction = new Direction(0, sensorManager, txtDirection);
        addSmartObject(direction);

        Colour colour = new Colour(0, layoutColor);
        addSmartObject(colour);

        AddressableTextDisplay textDisplay = new AddressableTextDisplay(0, txtDisplay);
        addSmartObject(textDisplay);

        String ipAddress = "0.0.0.0";

        String localAddress = ipAddress;
        int localPort = 5683;

        // get secure local address
        String secureLocalAddress = ipAddress;
        int secureLocalPort = 5684;



        InputStream is = getResources().openRawResource(R.raw.omaobjectsspec);
        LwM2mModel model = new LwM2mModel(ObjectLoader.loadJsonStream(is));


        // Initialize object list
        ObjectsInitializer initializer = new ObjectsInitializer(model);
        if(identity == null) {
            initializer.setInstancesForObject(SECURITY, noSecBootstap(bootstrapUrl));
        } else {
            initializer.setInstancesForObject(SECURITY, pskBootstrap(bootstrapUrl, identity.getBytes(), Hex.decodeHex(psk.toCharArray())));
            initializer.setInstancesForObject(SERVER, new Server(123, 3600, BindingMode.U, false));
        }

        List<Integer> enablerIds = new Vector<Integer>();
        enablerIds.add(SECURITY);
        enablerIds.add(SERVER);


        for(SmartObject smartObject : smartObjects) {
            initializer.setInstancesForObject(smartObject.getObjectId(), (BaseInstanceEnabler)smartObject);
            enablerIds.add(smartObject.getObjectId());
        }

        int[] enablerIdsArray = new int[enablerIds.size()];
        int index = 0;
        for(Integer r : enablerIds) {
            enablerIdsArray[index] = r;
            Log.d(TAG, "Enabler: " + r);
            index++;
        }

        List<LwM2mObjectEnabler> enablers = initializer.create(enablerIdsArray);

        // Create client
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        builder.setLocalAddress(localAddress, localPort);
        builder.setLocalSecureAddress(secureLocalAddress, secureLocalPort);
        builder.setObjects(enablers);

        client = builder.build();

        client.addObserver(new LwM2mClientObserver() {

            @Override
            public void onUpdateTimeout(DmServerInfo server) {
                Log.e(TAG, "###UPDATE TIME OUT");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "UPDATE TIMEOUT.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onUpdateSuccess(DmServerInfo server, String registrationID) {
                Log.d(TAG, "UPDATE SUCCESS");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "UPDATE SUCCESSFUL.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onUpdateFailure(DmServerInfo server, ResponseCode responseCode, final String errorMessage) {
                Log.e(TAG, "###UPDATE FAILURE");
                Log.e(TAG, "ResponseCode: " + responseCode.toString() + " : " + errorMessage);
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "UPDATE FAILURE. " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onRegistrationTimeout(DmServerInfo server) {
                Log.e(TAG, "###REGISTRATION TIME OUT");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "REGISTRATION TIMEOUT.", Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }

            @Override
            public void onRegistrationSuccess(final DmServerInfo server, String registrationID) {
                Log.d(TAG, "REGISTRATION SUCCESS");
                handler.post(new Runnable() {
                    public void run() {
                        txtDmUrl.setText(server.getFullUri().toString());
                        Toast.makeText(MainActivity.this, "REGISTRATION SUCCESSFUL.", Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(true);
            }

            @Override
            public void onRegistrationFailure(DmServerInfo server, ResponseCode responseCode, final String errorMessage) {
                Log.e(TAG, "###REGISTRATION FAILURE");
                Log.e(TAG, "ResponseCode: " + responseCode.toString() + " : " + errorMessage);
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "REGISTRATION FAILURE. " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }

            @Override
            public void onDeregistrationTimeout(DmServerInfo server) {
                Log.e(TAG, "###DEREGISTRATION TIME OUT");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "DEREGISTRATION TIMEOUT.", Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }

            @Override
            public void onDeregistrationSuccess(final DmServerInfo server, final String registrationID) {
                Log.d(TAG, "DEREGISTRATION SUCCESS");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "DEREGISTRATION SUCCESSFUL.", Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }

            @Override
            public void onDeregistrationFailure(DmServerInfo server, ResponseCode responseCode, final String errorMessage) {
                Log.e(TAG, "###DEREGISTRATION FAILURE");
                Log.e(TAG, "ResponseCode: " + responseCode.toString() + " : " + errorMessage);
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "DEREGISTRATION FAILURE. " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }

            @Override
            public void onBootstrapTimeout(ServerInfo bsserver) {
                Log.e(TAG, "###BOOTSTRAP TIME OUT");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "BOOTSTRAP TIMEOUT.", Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }

            @Override
            public void onBootstrapSuccess(ServerInfo bsserver) {
                Log.d(TAG, "BOOTSTRAP SUCCESS");
                // Handle registration service timer issue with android.
                client.stop(false);
                client.start();
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "BOOTSTRAP SUCCESSFUL.", Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }

            @Override
            public void onBootstrapFailure(ServerInfo bsserver, ResponseCode responseCode, final String errorMessage) {
                Log.e(TAG, "###BOOTSTRAP FAILURE");
                Log.e(TAG, "ResponseCode: " + responseCode.toString() + " : " + errorMessage);
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "BOOTSTRAP FAILED. " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
                setRegistered(false);
            }
        });
    }
}
