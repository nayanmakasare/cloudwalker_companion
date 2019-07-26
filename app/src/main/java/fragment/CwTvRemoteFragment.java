package fragment;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Set;

import appUtils.PreferenceManager;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.CwNsdListActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class CwTvRemoteFragment extends Fragment
{
    private NsdManager nsdManager;
    private PreferenceManager preferenceManager;
    private Set<String> linkedDevices;
    private static final String TAG = "CwTvRemoteFragment";
    private Socket primeSocket;
    private Vibrator vibe ;


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
            for (String devices : linkedDevices){
                if(serviceInfo.getServiceName().contains(devices)){
                    nsdManager.resolveService(serviceInfo, resolveListener);
                    break;
                }
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "onServiceLost: ");
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
            preferenceManager.setNsdHostAddress(serviceInfo.getHost().getHostAddress());
            preferenceManager.setNsdPort(serviceInfo.getPort());
            connectingToRemoteDevice();
        }
    };


    public void setNewDeviceForCommunication(String hostAddress, int port){
        preferenceManager.setNsdHostAddress(hostAddress);
        preferenceManager.setNsdPort(port);
        connectingToRemoteDevice();
    }

    private void connectingToRemoteDevice() {
        final String address = preferenceManager.getNsdHostAddress();
        final int port = preferenceManager.getNsdPort();
        if(address.isEmpty() || port > 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    primeSocket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(address, port);
                    try {
                        primeSocket.connect(socketAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void communication(final String message){
        if(primeSocket != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DataOutputStream os = new DataOutputStream(primeSocket.getOutputStream());
                        os.write(message.getBytes());
                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        preferenceManager = new PreferenceManager(getActivity());
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        nsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);
        connectingToLinkedDevice();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.cw_tv_remote_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.networkDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), CwNsdListActivity.class), 700);
            }
        });

        view.findViewById(R.id.okIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("23");
            }
        });

        view.findViewById(R.id.upIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("19");
            }
        });

        view.findViewById(R.id.downIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("20");
            }
        });

        view.findViewById(R.id.leftIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("21");
            }
        });

        view.findViewById(R.id.rightIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("22");
            }
        });


        view.findViewById(R.id.homeIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("3");
            }
        });


        view.findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("4");
            }
        });
    }

    private void connectingToLinkedDevice() {
        linkedDevices = preferenceManager.getLinkedNsdDevices();
        Log.d(TAG, "connectingToLinkedDevice: "+linkedDevices);
        if(linkedDevices == null) {
            //TODO implement Device discovery Activity
            Log.d(TAG, "connectingToLinkedDevice: show list activty");
        }
        else {
            nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        }
    }
}
