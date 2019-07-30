package fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

import appUtils.PreferenceManager;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.CwNsdListActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.PrimeActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.CwTvRemoteFragmentLayoutBinding;

public class CwTvRemoteFragment extends Fragment
{
    private NsdManager nsdManager;
    private PreferenceManager preferenceManager;
    private Set<String> linkedDevices;
    private static final String TAG = "CwTvRemoteFragment";
    private Vibrator vibe ;
    private CwTvRemoteFragmentLayoutBinding tvRemoteFragmentLayoutBinding;


    public void setNewDeviceForCommunication(String hostAddress, int port, String serviceName){
        Log.d(TAG, "setNewDeviceForCommunication: address "+hostAddress+" port     "+port+" "+serviceName);
        preferenceManager.setNsdHostAddress(hostAddress);
        preferenceManager.setNsdPort(port);
        preferenceManager.setCurrentNsdServiceConnected(serviceName);
        tvRemoteFragmentLayoutBinding.networkDevice.setText(serviceName);
    }

    private void communication(final String message){
        if(preferenceManager.getNsdPort() > 0 ){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket communicationSocket = new Socket();
                        communicationSocket.connect(new InetSocketAddress(preferenceManager.getNsdHostAddress(), preferenceManager.getNsdPort()));
                        DataOutputStream os = new DataOutputStream(communicationSocket.getOutputStream());
                        os.write(message.getBytes());
                        os.flush();
                        os.close();
                        communicationSocket.close();
                        Thread.currentThread().interrupt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            connectingToLinkedDevice();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getActivity());
        nsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);
        connectingToLinkedDevice();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tvRemoteFragmentLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.cw_tv_remote_fragment_layout, container, false);
        return  tvRemoteFragmentLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        tvRemoteFragmentLayoutBinding.networkDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), CwNsdListActivity.class), 700);
            }
        });

        tvRemoteFragmentLayoutBinding.okIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("23");
            }
        });

        tvRemoteFragmentLayoutBinding.upIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("19");
            }
        });

        tvRemoteFragmentLayoutBinding.downIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("20");
            }
        });

        tvRemoteFragmentLayoutBinding.leftIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("21");
            }
        });

        tvRemoteFragmentLayoutBinding.rightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("22");
            }
        });


        tvRemoteFragmentLayoutBinding.homeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("3");
            }
        });


        tvRemoteFragmentLayoutBinding.backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                communication("4");
            }
        });

        ((PrimeActivity)getActivity()).disableProgressBar();
    }

    private void connectingToLinkedDevice() {
        linkedDevices = preferenceManager.getLinkedNsdDevices();
        Log.d(TAG, "connectingToLinkedDevice: "+linkedDevices);
        if(linkedDevices == null) {
            Log.d(TAG, "connectingToLinkedDevice: show list activty");
            startActivityForResult(new Intent(getActivity(), CwNsdListActivity.class), 700);
        }else if(linkedDevices.size() == 0){
            startActivityForResult(new Intent(getActivity(), CwNsdListActivity.class), 700);
        } else {
            nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, new NsdManager.DiscoveryListener() {
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
                public void onServiceFound(final NsdServiceInfo serviceInfo) {
                    Log.d(TAG, "onServiceFound: "+serviceInfo.getServiceName());

                    for (String devices : linkedDevices){
                        if(serviceInfo.getServiceName().contains(devices)){
                            //found in linked Device
                            nsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                                @Override
                                public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                    Log.d(TAG, "onResolveFailed: ");
                                }

                                @Override
                                public void onServiceResolved(final NsdServiceInfo serviceInfo) {
                                    Log.d(TAG, "onServiceResolved: ");
                                    preferenceManager.setNsdHostAddress(serviceInfo.getHost().getHostAddress());
                                    preferenceManager.setNsdPort(serviceInfo.getPort());
                                    preferenceManager.setCurrentNsdServiceConnected(serviceInfo.getServiceName());

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "Got linked device resolving.", Toast.LENGTH_SHORT).show();
                                            tvRemoteFragmentLayoutBinding.networkDevice.setText(serviceInfo.getServiceName());
                                        }
                                    });
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onServiceLost(NsdServiceInfo serviceInfo) {
                    Log.d(TAG, "onServiceLost: ");
                }
            });
        }
    }
}
