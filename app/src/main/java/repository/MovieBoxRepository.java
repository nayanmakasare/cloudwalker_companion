package repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import api.CustomHttpClient;
import api.MyProfileInterface;
import api.ServiceGenerator;
import model.MovieResponse;
import model.MovieRow;
import model.MovieTile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieBoxRepository
{
    private static final String TAG = "MovieBoxRepository";

    private MutableLiveData<List<MovieTile>> liveMovieTile = new MutableLiveData<>();

    public MutableLiveData<List<MovieTile>> getLiveMovieTile(){
        return liveMovieTile;
    }

    private MutableLiveData<Boolean> nsdMessageSentStatus = new MutableLiveData<>();

    public MovieBoxRepository(Context context){
        populateData(context);
    }

    private void populateData(Context context) {
      new Retrofit.Builder()
                .baseUrl("http://192.168.1.143:9876/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyProfileInterface.class)
              .getHomeScreenData()
              .enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.code() == 200) {
                    List<MovieTile> movieTiles = new ArrayList<>();
                    for (MovieRow movieRow : response.body().getRows()) {
                        for (MovieTile movieTile : movieRow.getRowItems()) {
                            movieTiles.add(movieTile);
                        }
                    }
                    liveMovieTile.postValue(movieTiles);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private class SocketConnection {
        private Socket mSocket;
        public void sayHello(final String host, final int port, final  String nsdMessage){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mSocket = new Socket();
                    if(host == null){
                        return;
                    }
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
                        } catch (Exception e) {
                            e.printStackTrace();
                            nsdMessageSentStatus.postValue(false);
                        }
                        os.close();
                        is.close();
                    } catch (IOException e) {e.printStackTrace();}
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
