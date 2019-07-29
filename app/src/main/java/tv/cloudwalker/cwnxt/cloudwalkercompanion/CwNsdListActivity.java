package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapter.CwNsdAdapter;
import api.ServiceGenerator;
import appUtils.PreferenceManager;
import model.TvInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityCwNsdListBinding;
import utils.NetworkUtils;

public class CwNsdListActivity extends AppCompatActivity {

    private ActivityCwNsdListBinding activityCwNsdListBinding;
    private NsdManager nsdManager;
    private static final String TAG = "CwNsdListActivity";
    private List<NsdServiceInfo> nsdServiceInfoList = new ArrayList<>();
    private CwNsdAdapter cwNsdAdapter = new CwNsdAdapter();
    private SocketConnection socketConnection;
    private NsdServiceInfo nsdLinkedServiceInfo;
    private PreferenceManager preferenceManager;

    private NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {
        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.d(TAG, "onStartDiscoveryFailed: ");
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.d(TAG, "onStopDiscoveryFailed: ");
        }

        @Override
        public void onDiscoveryStarted(String serviceType) {
            Log.d(TAG, "onDiscoveryStarted: ");
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.d(TAG, "onDiscoveryStopped: ");
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "onServiceFound: "+serviceInfo.getServiceName());
            nsdServiceInfoList.add(serviceInfo);
            CwNsdListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cwNsdAdapter.submitList(nsdServiceInfoList);
                }
            });
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "onServiceLost: "+serviceInfo.getServiceName());
            if(nsdServiceInfoList.contains(serviceInfo)){
                nsdServiceInfoList.remove(serviceInfo);
            }
        }
    };


    private NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.d(TAG, "onResolveFailed: ");
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "onServiceResolved: ");
            nsdLinkedServiceInfo = serviceInfo;
            socketConnection =  new SocketConnection();
            socketConnection.sayHello(serviceInfo.getHost().getHostAddress(), serviceInfo.getPort(), "sendTvInfo");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCwNsdListBinding = DataBindingUtil.setContentView(this, R.layout.activity_cw_nsd_list);
        preferenceManager = new PreferenceManager(this);
        Set<String> linkedDevice = preferenceManager.getLinkedNsdDevices();
        if(linkedDevice != null){
            for(String s : linkedDevice){
                Log.d(TAG, "onCreate: linked devices "+s);
            }
        }
        getSupportActionBar().hide();
        nsdManager = (NsdManager)getSystemService(Context.NSD_SERVICE);
        activityCwNsdListBinding.linkedDeviceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        activityCwNsdListBinding.linkedDeviceList.setAdapter(cwNsdAdapter);
        nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

        cwNsdAdapter.setOnItemClickedListener(new CwNsdAdapter.OnItemClickListener() {
            @Override
            public void onDeviceClicked(NsdServiceInfo nsdServiceInfo) {
                Set<String> linkedDevices  = preferenceManager.getLinkedNsdDevices();
                if(linkedDevices != null && linkedDevices.size() > 0 && linkedDevices.contains(nsdServiceInfo.getServiceName())){
                    Toast.makeText(getApplicationContext(), "This device is already Linked", Toast.LENGTH_LONG).show();
                    preferenceManager.setNsdHostAddress(nsdServiceInfo.getHost().getHostAddress());
                    preferenceManager.setNsdPort(nsdServiceInfo.getPort());
                    finish();
                }else {
                    nsdManager.resolveService(nsdServiceInfo, resolveListener);
                }
            }
        });
    }

    private void registerDevice(String infoString)
    {
        if(NetworkUtils.getConnectivityStatus(this) > 0) {
            Log.d(TAG, "registerDevice: ");
            final String[] contentPices = infoString.split("~");
            final TvInfo tvInfo = new TvInfo(contentPices[4],contentPices[1], contentPices[2] );
            final PreferenceManager preferenceManager = new PreferenceManager(this);
            ServiceGenerator.getRequestApi().postNewTvDevice(tvInfo, preferenceManager.getGoogleId()).enqueue(new Callback<TvInfo>() {
                @Override
                public void onResponse(Call<TvInfo> call, Response<TvInfo> response) {
                    Log.d(TAG, "onResponse: "+response.code());
                        if(response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "Linked Device Successfully", Toast.LENGTH_SHORT).show();
                            socketConnection.sayHello(nsdLinkedServiceInfo.getHost().getHostAddress(), nsdLinkedServiceInfo.getPort(), "showProfile");
                            try {
                                socketConnection.mSocket.close();
                                socketConnection.stopCommunicationThread();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Set<String> linkedDevice = preferenceManager.getLinkedNsdDevices();
                            if(linkedDevice == null){
                                linkedDevice = new HashSet<>();
                            }
                            if(!linkedDevice.contains(tvInfo.getEmac())){
                                Log.d(TAG, "onResponse: added linked device in prefrence.");
                                linkedDevice.add(tvInfo.getEmac());
                            }
                            preferenceManager.setLinkedNsdDevices(linkedDevice);
                            Intent data = new Intent(CwNsdListActivity.this, PrimeActivity.class);
                            Log.d(TAG, "onResponse: nsdAddress "+nsdLinkedServiceInfo.getHost().getHostAddress());
                            Log.d(TAG, "onResponse: port "+nsdLinkedServiceInfo.getPort());
                            data.putExtra("nsdAddress", nsdLinkedServiceInfo.getHost().getHostAddress());
                            data.putExtra("port", nsdLinkedServiceInfo.getPort());
                            setResult(RESULT_OK, data);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                        }
                }

                @Override
                public void onFailure(Call<TvInfo> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "Not connected to internet. Cannot link your device", Toast.LENGTH_SHORT).show();
        }
    }

    private class SocketConnection {
        private Socket mSocket;
        private Thread socketCommunicationThread;

        public void sayHello(final String host, final int port, final String nsdMessage) {

           socketCommunicationThread =  new Thread(new Runnable() {
                @Override
                public void run() {
                    mSocket = new Socket();
                    SocketAddress address = new InetSocketAddress(host, port);
                    try {
                        Log.e("TrackingFlow", "Trying to connect to: " + host);
                        mSocket.connect(address);
                        DataOutputStream os = new DataOutputStream(mSocket.getOutputStream());
                        DataInputStream is = new DataInputStream(mSocket.getInputStream());
                        //Send a message...
                        os.write(nsdMessage.getBytes());
                        os.flush();
                        Log.e("TrackingFlow", "Message SENT!!!");

                        //Read the message
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        StringBuilder sb = new StringBuilder();
                        int length = Integer.MAX_VALUE;
                        try {
                            while (length >= bufferSize) {
                                length = is.read(buffer);
                                sb.append(new String(buffer, 0, length));
                            }
                            final String receivedMessage = sb.toString();
                            Log.d(TAG, "run: recevice message " + receivedMessage);

                            if (receivedMessage.contains("tvinfo~")) {
                                Log.d(TAG, "run: recevice in if ");
                                String[] contentPices = receivedMessage.split("~");
                                boolean result = isDeviceLinked(contentPices[4]);
                                if (!result){
                                    Log.d(TAG, "run: linking device");
                                    registerDevice(receivedMessage);
                                }
                            }else if(receivedMessage.contains("showProfile")){
                                os.close();
                                is.close();
                                mSocket.close();
                                stopCommunicationThread();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            os.close();
                            is.close();
                            mSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

           socketCommunicationThread.start();
        }

        public void stopCommunicationThread(){
            if(socketCommunicationThread != null && socketCommunicationThread.isAlive()){
                Log.d(TAG, "stopCommunicationThread: stoping ");
                socketCommunicationThread.interrupt();
            }
        }
    }



    private boolean isDeviceLinked(String tvEmac) {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        Set<String> linkedDevices = preferenceManager.getLinkedNsdDevices();
        if(linkedDevices == null) {
            return false;
        }
        else {
            return linkedDevices.contains(tvEmac);
        }
    }
}
