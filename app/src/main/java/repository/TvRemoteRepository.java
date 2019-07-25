package repository;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TvRemoteRepository
{
    private static final String TAG = "TvRemoteRepository";
    private MutableLiveData<Boolean> nsdMessageSentStatus = new MutableLiveData<>();

    public TvRemoteRepository(){

    }

    private class SocketConnection {
        private Socket mSocket;
        public void sayHello(final String host, final int port, final  String nsdMessage){
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
                        nsdMessageSentStatus.postValue(true);

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
                            Log.d(TAG, "run: recevice message "+receivedMessage);

//                            if(receivedMessage.contains("tvinfo~"))
//                            {
//                                Log.d(TAG, "run: recevice in if ");
//                                Set<String> deviceList = preferenceManager.getLinkedNsdDevices();
//                                Log.d(TAG, "run: got device list ");
//                                boolean isDeviceLinked = false;
//                                if(deviceList != null && !deviceList.isEmpty() && deviceList.size() > 0)
//                                {
//                                    for(String device : deviceList) {
//                                        if(receivedMessage.contains(device)) {
//                                            isDeviceLinked = true;
//                                            break;
//                                        }
//                                    }
//                                }
//
//                                Log.d(TAG, "run: linked result "+isDeviceLinked);
//                                if(!isDeviceLinked) {
//                                    registerDevice(receivedMessage);
//                                }
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            nsdMessageSentStatus.postValue(false);
                        }
                        os.close();
                        is.close();
                    } catch (IOException e) {
                        nsdMessageSentStatus.postValue(false);
                        e.printStackTrace();}
                }
            }).start();
        }
    }

    public void sendMessageFromNSD(String host , int port, String message) {
        new SocketConnection().sayHello(host, port, message);
    }

    public MutableLiveData<Boolean> getNsdMessageSendStatus(){
        return nsdMessageSentStatus;
    }

}
