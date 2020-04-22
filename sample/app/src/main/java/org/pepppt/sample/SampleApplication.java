package org.pepppt.sample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.pepppt.core.CoreSystems;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.ble.profiles.AdvertiserProfile;
import org.pepppt.core.ble.profiles.ScannerProfile;
import org.pepppt.core.exceptions.InvalidBackendEndpointException;
import org.pepppt.core.exceptions.InvalidBackendEndpointFormatException;
import org.pepppt.core.exceptions.MissingBackendEndpointException;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.ui.utils.PropertyReader;
import org.pepppt.sample.ui.utils.ServiceNotification;

import java.util.Properties;

public class SampleApplication extends Application {
    public static SampleApplication instance;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static long started;
    public static String endpoints = "{ \"endpoints\":[ " +
            "{ \"backend_API_Base_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_broadcastkeys\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_GetMe_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_registration_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_auth_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_getTan_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_finish_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_testLab_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_sendData_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_checkMessages_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_getMessage_Url\" : \"ENDPOINT_URL\" }, " +
            "{ \"backend_API_confirmMessage_Url\" : \"ENDPOINT_URL\" } " +

            "] }";
    // TODO: Issue#10: [BSI] No State of the Art Certificate Pinning: pin-expiration time missing
    private static String host;
    private static String[] certfp;

    private ProximityTracingService proximityservice;

    private PropertyReader propertyReader;
    private Properties properties;

    public static SampleApplication getApplication() {
        return instance;
    }

    public ProximityTracingService getProximityService() {
        return proximityservice;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        started = System.currentTimeMillis();
        instance = this;
        host = getContext().getString(R.string.cert_host);
        certfp = new String[]{
                getContext().getString(R.string.certfp_1),
                getContext().getString(R.string.certfp_2),
                getContext().getString(R.string.certfp_3),
        };
        try {
            proximityservice = new ProximityTracingService(getContext());
        } catch (PackageNotFoundException ex) {
            ex.printStackTrace();
            Log.e("SampleApplication", ex.toString());
        }
        proximityservice.setCallback(new MyCallback());
        propertyReader = new PropertyReader(getContext());
        properties = propertyReader.getMyProperties("app.properties");

        /**
         * load the endpoints from file or from a static string.
         */
        try {
            proximityservice.setApiEndpoints(endpoints);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidBackendEndpointException e) {
            e.printStackTrace();
        } catch (MissingBackendEndpointException e) {
            e.printStackTrace();
        } catch (InvalidBackendEndpointFormatException e) {
            e.printStackTrace();
        }
        proximityservice.setClientData(properties.getProperty("client_id"),
                properties.getProperty("client_secret"));
        proximityservice.setCertificatePinnerInformation(host, certfp);
        proximityservice.setScannerProfile(ScannerProfile.SCANNER_PROFILE_LOWLATENCY);
        proximityservice.setAdvertiserProfile(AdvertiserProfile.ADVERTISER_PROFILE_1);
        proximityservice.setForegroundServiceNotification(new ServiceNotification(
                getString(R.string.appchannelid),
                getString(R.string.appchannelname),
                getString(R.string.appchanneldesc)).createNotification(this,
                getString(R.string.service_notification_title),
                getString(R.string.service_notification_text),
                R.mipmap.ic_app_icon));
        proximityservice.setBluetoothKeepAlive(true);
        proximityservice.setEnableTelemetry(true);
        proximityservice.start();
    }

}
