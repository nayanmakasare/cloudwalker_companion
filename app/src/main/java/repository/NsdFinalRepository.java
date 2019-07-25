package repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import model.TvInfo;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.NetworkUtils;

public class NsdFinalRepository
{

    private NsdManager mNsdManager;
    private PreferenceManager preferenceManager;
    private static final String TAG = "NsdFinalRepository";
    private List<NsdServiceInfo> nsdServiceInfos = new ArrayList<>();
    private List<TvInfo> linkedTvInfoList ;
    private MutableLiveData<List<NsdServiceInfo>> liveNsdDeviceList = new MutableLiveData<>();
    private MutableLiveData<NsdServiceInfo> nsdResolveStatus = new MutableLiveData<>();

    public MutableLiveData<Boolean> getNsdDiscoveryStatus() {
        return nsdDiscoveryStatus;
    }

    private MutableLiveData<Boolean> nsdDiscoveryStatus = new MutableLiveData<>();
    private String mHostFound;
    private int mPortFound;
    private Context context;

    public MutableLiveData<List<NsdServiceInfo>> getLiveNsdDeviceList() {
        return liveNsdDeviceList;
    }

    public MutableLiveData<NsdServiceInfo> getNsdResolveStatus() {
        return nsdResolveStatus;
    }




    public NsdFinalRepository(Context context) {
        this.preferenceManager = new PreferenceManager(context);
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        this.context = context;
        getAllLinkedDevices();
    }

    public void stopNsdDiscovery(){
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public void startNsdDiscovery(){
        mNsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }


    public void settingNsdDiscovery() {
        Log.d(TAG, "settingNsdDiscovery: ");
        startNsdDiscovery();
    }

    public void setLinkedTvInfoList(List<TvInfo> tvInfos){
        linkedTvInfoList = tvInfos;
    }


    private NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {
    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.d(TAG, "onStartDiscoveryFailed: "+ errorCode);
        nsdDiscoveryStatus.postValue(false);

    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.d(TAG, "onStopDiscoveryFailed: ");
    }

    @Override
    public void onDiscoveryStarted(String serviceType) {
        Log.d(TAG, "onDiscoveryStarted: ");
        nsdDiscoveryStatus.postValue(true);
    }

    @Override
    public void onDiscoveryStopped(String serviceType) {
        Log.d(TAG, "onDiscoveryStopped: ");
    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "onServiceFound: " + serviceInfo.getServiceName());
        nsdServiceInfos.add(serviceInfo);
        liveNsdDeviceList.postValue(nsdServiceInfos);
        if(linkedTvInfoList != null && linkedTvInfoList.size() > 0){
            for(TvInfo tvInfo : linkedTvInfoList) {
                if(serviceInfo.getServiceName().contains(tvInfo.getEmac())) {
                    Log.d(TAG, "onServiceFound: linked device Found ");
                    resolveNsdService(serviceInfo, true);
                    nsdResolveStatus.postValue(serviceInfo);
                    break;
                }
            }
        }
    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {
        for (int i = 0; i < nsdServiceInfos.size(); i++) {
            if (nsdServiceInfos.get(i).getServiceName().equals(serviceInfo.getServiceName())) {
                nsdServiceInfos.remove(i);
                break;
            }
        }
        liveNsdDeviceList.postValue(nsdServiceInfos);
    }
 };



    private boolean isDeviceLinked(String tvEmac) {
        if(linkedTvInfoList != null && linkedTvInfoList.size() > 0){
            for(TvInfo tvInfo : linkedTvInfoList){
                if(tvEmac.contains(tvInfo.getEmac())){
                    return true;
                }
            }
        }
        return false;
    }


    public void resolveNsdService(final NsdServiceInfo nsdServiceInfo, final boolean linkedStatus) {
        mNsdManager.resolveService(nsdServiceInfo, new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "onResolveFailed: ");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "onServiceResolved: ");
                nsdResolveStatus.postValue(serviceInfo);
                setHostAndPortValues(serviceInfo);
                if(!linkedStatus && !isDeviceLinked(serviceInfo.getServiceName())) {
                    Log.d(TAG, "onServiceResolved: device not linked");
                    sayHello("sendTvInfo");
                }
            }
        });
    }

    private void setHostAndPortValues(NsdServiceInfo serviceInfo) {
        mHostFound = serviceInfo.getHost().getHostAddress();
        mPortFound = serviceInfo.getPort();
        Log.d(TAG, "setHostAndPortValues: " + mHostFound + " " + mPortFound);
    }

    private void getAllLinkedDevices() {
        Log.d(TAG, "getAllLinkedDevices: ");
        if (NetworkUtils.getConnectivityStatus(context) > 0)
        {
            Log.d(TAG, "getAllLinkedDevices: 1 ");
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);

            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.143:5081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MyProfileInterface.class)
                    .getLinkDevices(preferenceManager.getGoogleId())
                    .enqueue(new Callback<List<TvInfo>>() {
                        @Override
                        public void onResponse(Call<List<TvInfo>> call, Response<List<TvInfo>> response) {
                            Log.d(TAG, "onResponse: linked device call "+response.code()+" "+response.body().size());
                            if(response.code() == 200 && response.body().size() > 0) {
                                linkedTvInfoList = response.body();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<TvInfo>> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t.getMessage());
                        }
                    });
        }
    }

    private void sayHello(String message) {
        Log.d(TAG, "sayHello: " + mHostFound + " " + mPortFound);
        if (mHostFound == null || mPortFound <= 0) {
            Log.e(TAG, "sayHello: no service found ");
            return;
        }
        new NsdFinalRepository.SocketConnection().sayHello(mHostFound, mPortFound, message);
    }

    private class SocketConnection {
        private Socket mSocket;

        public void sayHello(final String host, final int port, final String nsdMessage) {
            new Thread(new Runnable() {
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
                                if (!isDeviceLinked(contentPices[4])) {
                                    registerDevice(receivedMessage);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        os.close();
                        is.close();
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void registerDevice(String infoString) {
        Log.d(TAG, "registerDevice: " + infoString);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);

        final String[] contentPices = infoString.split("~");
        final TvInfo tvInfo = new TvInfo(contentPices[4],contentPices[1], contentPices[2] );
        if (NetworkUtils.getConnectivityStatus(context) > 0)
        {
            Log.d(TAG, "registerDevice: google id "+preferenceManager.getGoogleId());
            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.143:5081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build()
                    .create(MyProfileInterface.class)
                    .postNewTvDevice(tvInfo, preferenceManager.getGoogleId())
                    .enqueue(new Callback<TvInfo>() {
                        @Override
                        public void onResponse(Call<TvInfo> call, Response<TvInfo> response) {
                            Log.d(TAG, "onResponse: " + response.code());
                            if (response.code() == 200) {
                                sayHello("showProfile");
                            }
                            else {
                                try {
                                    Log.d(TAG, "onResponse: error body "+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TvInfo> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }
                    });

        } else {
            Toast.makeText(context, "Otp verification Process failed. Please check your Internet connection.", Toast.LENGTH_LONG).show();
        }
    }
}
