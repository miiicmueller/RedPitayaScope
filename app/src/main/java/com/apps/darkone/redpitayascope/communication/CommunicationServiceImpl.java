package com.apps.darkone.redpitayascope.communication;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.darkone.redpitayascope.communication.commSAP.ICommunicationService;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnConnectionListener;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnDataListener;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnParamListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by DarkOne on 06.10.15.
 */
public class CommunicationServiceImpl implements ICommunicationService, Runnable {


    private List<IOnDataListener> mDataListenerList;
    private List<IOnParamListener> mParamListenerList;
    private List<IOnConnectionListener> mConnectionListenerList;

    private CommunicationState mCommState;
    private String mAppName;
    private boolean mIsRunning;
    private boolean mAppStartRequested;
    private boolean mAppStopRequested;


    private int mConnectionState; // Give the connection state. In case we cannot connect to the server...
    private String mBoardIpAdress;
    private Thread mConnectionThread;
    private RequestQueue mRequestQueueGetData;
    private RequestQueue mRequestQueuePost;
    private Context mContext;
    private Cache mCache;
    private BasicNetwork mNetwork;

    // Tools
    private static final int POLL_TIME_MS = 150; // We are waiting X ms to get the datas
    private static final String COMM_IMPL_TAG = "CommuncationImpl";

    private static final String JSON_FIELD_APP = "app";
    private static final String JSON_FIELD_APP_ID = "id";
    private static final String JSON_FIELD_STATUS = "status";
    private static final String JSON_FIELD_DATASET = "datasets";
    private static final String JSON_FIELD_POINTS = "g1";
    private static final String JSON_FIELD_POINTS_CHAN = "data";
    private static final String JSON_FIELD_PARAMS = "params";


    public static final int HARWARE_CONNECTION_OK = 0;
    public static final int HARWARE_CONNECTION_ERROR = -1;
    public static final int HARDWARE_CONNECTION_PROBE = 1;


    /*
        Constructors
     */
    public CommunicationServiceImpl(String defaultIpAddress) {
        this.mDataListenerList = new ArrayList<IOnDataListener>();
        this.mParamListenerList = new ArrayList<IOnParamListener>();
        this.mConnectionListenerList = new ArrayList<IOnConnectionListener>();

        this.mBoardIpAdress = defaultIpAddress;
        this.mIsRunning = false;
        this.mAppName = "";
        mConnectionState = HARDWARE_CONNECTION_PROBE;


        Log.d(COMM_IMPL_TAG, "CommunicationServiceImpl created");
    }

    @Override
    public void startService(String ipAddress, Context context) {

        this.mBoardIpAdress = ipAddress;
        this.mContext = context;

        Log.d(COMM_IMPL_TAG, "Starting communication service at " + mBoardIpAdress + "...");

        // We only start the service if the service is stopped
        if (!this.isServiceRunning()) {

            Log.d(COMM_IMPL_TAG, "Service not running. Starting normally...");
            startConnectionThread();
        }
        // Else we restart service
        else {
            Log.d(COMM_IMPL_TAG, "Service is running. Restarting service...");
            stopService();
            startConnectionThread();

        }
    }

    private void startConnectionThread() {

        Log.d(COMM_IMPL_TAG, "Starting connection thread...");

        // Instantiate the cache
        this.mCache = new DiskBasedCache(this.mContext.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        this.mNetwork = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        this.mRequestQueueGetData = new RequestQueue(mCache, mNetwork);

        this.mRequestQueuePost = Volley.newRequestQueue(mContext);

        // Start the queue
        this.mRequestQueueGetData.start();

        // Service is now in running mode
        this.mIsRunning = true;
        this.mCommState = CommunicationState.waitingForConnect;

        // Create the connection thread
        this.mConnectionThread = new Thread(this);

        // Start the thread
        this.mConnectionThread.start();
    }

    @Override
    public void stopService() {

        Log.d(COMM_IMPL_TAG, "Stopping service...");

        try {
            if (isServiceUsed()) {
                stopActualRunningApp();
            }

            this.mIsRunning = false;

            //Wait the thread for quit
            this.mConnectionThread.join();

            //Stop the request queue
            mRequestQueueGetData.stop();
            mRequestQueuePost.stop();

            //Clear the cache
            mRequestQueueGetData.getCache().clear();
            mRequestQueuePost.getCache().clear();

        } catch (InterruptedException e) {
            Log.e(COMM_IMPL_TAG, "Service Thread error : " + e.toString());
        }
    }

    @Override
    public boolean isServiceRunning() {
        return this.mIsRunning;
    }

    @Override
    public boolean isServiceUsed() {
        return (this.mCommState != CommunicationState.waitingForConnect);
    }

    @Override
    public void startApp(String appName) {

        Log.d(COMM_IMPL_TAG, "Request application start : " + appName);

        this.mAppName = appName;
        this.mAppStartRequested = true;
    }

    @Override
    public void stopActualRunningApp() {
        Log.d(COMM_IMPL_TAG, "Application disconnection requested...");
        this.mAppStopRequested = true;

        //Wait for the state machine to be disconnected
        while (mCommState != CommunicationState.waitingForConnect) ;

        // Callback all listener for this event
        notifyConnectionEvents(EventCodeEnum.DISCONNECTED, mAppName);
    }

    @Override
    public String getActualRunningAppName() {
        return this.mAppName;
    }

    @Override
    public void addOnDataListener(IOnDataListener dataListener) {
        this.mDataListenerList.add(dataListener);
    }

    @Override
    public void removeOnDataListener(IOnDataListener dataListener) {
        this.mDataListenerList.remove(dataListener);
    }

    @Override
    public void addOnParamListener(IOnParamListener paramListener) {
        this.mParamListenerList.add(paramListener);
    }

    @Override
    public void removeOnParamListener(IOnParamListener paramListener) {
        this.mParamListenerList.remove(paramListener);
    }

    @Override
    public void asyncNewParamsPost(String appName, final JSONObject params) {
        // First check if the app is the running one + service is UP + communication state is running

        if (mAppName.equals(appName) && isServiceRunning() && mCommState == CommunicationState.running) {

            String url = "http://" + mBoardIpAdress + "/data";

            Log.d(COMM_IMPL_TAG, "Post new request...");

//            StringRequest strRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d(COMM_IMPL_TAG, "POST Response : " + response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d(COMM_IMPL_TAG, "POST Response Error : " + error.toString());
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> parameterFormPost = new HashMap<String, String>();
//
//                    parameterFormPost.put(params.toString(), "");
//                    return parameterFormPost;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> parameterFormPost = new HashMap<String, String>();
//                    parameterFormPost.put("Pragma", "no-cache");
//                    parameterFormPost.put("Cache-Control", "no-cache");
//                    return parameterFormPost;
//                }
//            };
//
//            strRequest.setShouldCache(false);
//            mRequestQueuePost.add(strRequest);

            // Create a new HttpClient and Post Header
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                readStream(in);
                finally {
                    urlConnection.disconnect();
                }
            }
    }

    @Override
    public void addOnConnectionListener(IOnConnectionListener connectionListener) {
        this.mConnectionListenerList.add(connectionListener);
    }

    @Override
    public void removeOnConnectionListener(IOnConnectionListener connectionListener) {
        this.mConnectionListenerList.remove(connectionListener);
    }

    @Override
    public Integer getConnectionStatus() {
        return mConnectionState;
    }

    // PRIVATE METHODS

    private void notifyConnectionEvents(EventCodeEnum appStarted, String mAppName) {
        ConnectionEvent event = new ConnectionEvent(appStarted);
        for (IOnConnectionListener listener : mConnectionListenerList) {
            listener.onConnectionChanged(mAppName, event);
        }
    }


    /**
     * Working thread to ask for data and set the parameters
     */
    @Override
    public void run() {

        String url;
        JsonObjectRequest jsObjRequest;

        Log.d(COMM_IMPL_TAG, "Thread started");

        while (true == mIsRunning) {

            switch (mCommState) {
                case waitingForConnect:
                    mConnectionState = HARDWARE_CONNECTION_PROBE;
                    // We do nothing here...
                    if (mAppStartRequested) {
                        mCommState = CommunicationState.connecting;

                        // Callback all listener for this event
                        notifyConnectionEvents(EventCodeEnum.STARTING_APP, mAppName);
                    }

                    break;
                case connecting:

                    mAppStartRequested = false;

                    // Create the connection request
                    url = "http://" + mBoardIpAdress + "/bazaar?start=" + mAppName;

                    if (mAppStopRequested) {

                        mAppStopRequested = false;

                        Log.d(COMM_IMPL_TAG, "Connecting, but asked to disconnect...");
                        mCommState = CommunicationState.disconnecting;
                    } else {
                        jsObjRequest = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        String status;

                                        try {
                                            status = response.getString(JSON_FIELD_STATUS);

                                            if (status.equals("OK")) {
                                                Log.d(COMM_IMPL_TAG, "Response: " + response.toString());

                                                // Connection was possible
                                                mConnectionState = HARWARE_CONNECTION_OK;

                                                // Callback all listener for this event
                                                notifyConnectionEvents(EventCodeEnum.APP_STARTED, mAppName);

                                            } else {
                                                Log.d(COMM_IMPL_TAG, "Response: " + response.toString());

                                                // Connected, but app failed to start
                                                mConnectionState = HARWARE_CONNECTION_ERROR;

                                                // Callback all listener for this event
                                                notifyConnectionEvents(EventCodeEnum.APP_START_FAILED, mAppName);
                                            }

                                        } catch (JSONException e) {
                                            Log.e(COMM_IMPL_TAG, "Response: format error!");
                                        }


                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(COMM_IMPL_TAG, "Error: " + error.toString());

                                        // Callback all listener for this event
                                        notifyConnectionEvents(EventCodeEnum.CONNECTION_ERROR, mAppName);

                                        mConnectionState = HARWARE_CONNECTION_ERROR;
                                    }
                                });


                        // Access the RequestQueue through your singleton class.
                        mRequestQueueGetData.add(jsObjRequest);
                        mCommState = CommunicationState.waitConnectingResponse;
                    }
                    break;

                case waitConnectingResponse:
                    // We only wait here
                    if (mConnectionState == HARWARE_CONNECTION_ERROR) {

                        // Retry the connection
                        mConnectionState = HARDWARE_CONNECTION_PROBE;
                        mCommState = CommunicationState.connecting;

                    } else if (mConnectionState == HARWARE_CONNECTION_OK) {
                        mCommState = CommunicationState.running;
                    }
                    break;
                case running:


                    // Create the connection request
                    url = "http://" + mBoardIpAdress + "/data";

                    jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    String status;
                                    String responseAppName;
                                    JSONObject jsonTmpObj;

                                    JSONArray jsonObjDataChannels;
                                    JSONObject jsonParamsObj;


                                    List<List<Map<Double, Double>>> channelsDatasContainer;
                                    List<Map<Double, Double>> channelDataList;
                                    List<Map<String, Object>> paramsList;


                                    try {
                                        status = response.getString(JSON_FIELD_STATUS);

                                        // Get the app id
                                        jsonTmpObj = response.getJSONObject(JSON_FIELD_APP);
                                        responseAppName = jsonTmpObj.getString(JSON_FIELD_APP_ID);


                                        // Get the dataset
                                        jsonTmpObj = response.getJSONObject(JSON_FIELD_DATASET);


                                        // Get the params
                                        jsonParamsObj = jsonTmpObj.getJSONObject(JSON_FIELD_PARAMS);


                                        // We get the two channel datas
                                        channelsDatasContainer = new ArrayList<>();
                                        JSONArray array = jsonTmpObj.getJSONArray(JSON_FIELD_POINTS);

                                        for (int i = 0; i <
                                                jsonTmpObj.getJSONArray(JSON_FIELD_POINTS).length(); i++) {
                                            jsonObjDataChannels = array.getJSONObject(i).getJSONArray(JSON_FIELD_POINTS_CHAN);

                                            Log.d(COMM_IMPL_TAG, "Received Ch" + i + " : " + jsonObjDataChannels.toString());

                                            channelDataList = new ArrayList<>();

                                            for (int j = 0; j < jsonObjDataChannels.length(); j++) {
                                                JSONArray dataTuple = jsonObjDataChannels.getJSONArray(j);

                                                Map<Double, Double> dataTupleMap = new TreeMap<>();
                                                // Add the point to the Map
                                                dataTupleMap.put(dataTuple.getDouble(0), dataTuple.getDouble(1));

                                                // Add the map to the list
                                                channelDataList.add(dataTupleMap);
                                            }

                                            // Add the value to the container List
                                            channelsDatasContainer.add(channelDataList);
                                        }

                                        Log.d(COMM_IMPL_TAG, "Data status : " + status.toString());

                                        // Callback the values
                                        for (IOnDataListener listener : mDataListenerList) {
                                            listener.newDataAvailable(responseAppName, channelsDatasContainer);
                                        }

                                        // Callback the params
                                        for (IOnParamListener listener : mParamListenerList) {
                                            listener.newParamsAvailable(responseAppName, jsonParamsObj);
                                        }


                                    } catch (JSONException e) {
                                        Log.e(COMM_IMPL_TAG, "Response: format error! " + e.toString());
                                    }

                                    // Connection was possible
                                    mConnectionState = HARWARE_CONNECTION_OK;

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(COMM_IMPL_TAG, "Error: " + error.toString());

                                    // Callback all listener for this event
                                    notifyConnectionEvents(EventCodeEnum.CONNECTION_ERROR, mAppName);

                                    // We try to reconnect
                                    mConnectionState = HARDWARE_CONNECTION_PROBE;
                                    mCommState = CommunicationState.connecting;

                                }
                            });

                    // Access the RequestQueue through your singleton class.
                    mRequestQueueGetData.add(jsObjRequest);

                    // Check if we want to stop the app
                    if (mAppStopRequested) {
                        mCommState = CommunicationState.disconnecting;
                    }
                    break;
                case disconnecting:

                    mAppStopRequested = false;

                    // Create the connection request
                    url = "http://" + mBoardIpAdress + "/bazaar?stop=" + mAppName;
                    jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // We don't care about the response
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(COMM_IMPL_TAG, "Error: " + error.toString());
                                }
                            });

                    // Connection status reset
                    mConnectionState = HARWARE_CONNECTION_OK;

                    // Access the RequestQueue through your singleton class.
                    mRequestQueueGetData.add(jsObjRequest);
                    mCommState = CommunicationState.waitingForConnect;
                    break;
                default:
                    break;
            }

            try {
                Thread.sleep(POLL_TIME_MS);
            } catch (InterruptedException e) {
                Log.e(COMM_IMPL_TAG, "Thread sleep error!");
            }
        }


        Log.d(COMM_IMPL_TAG, "Thread stopped...");
    }
}
