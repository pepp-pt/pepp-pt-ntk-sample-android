package org.pepppt.sample.ui.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


// This class makes sure that there is only one
// instance of RequestQueue as it should be instantiated only once
public class RequestQueueSingleton {
    private static RequestQueueSingleton requestQueueSingleton;
    private RequestQueue requestQueue;
    private static Context context;

    private RequestQueueSingleton(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (requestQueueSingleton == null) {
            requestQueueSingleton = new RequestQueueSingleton(context);
        }
        return requestQueueSingleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

}
