package repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import java.util.List;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import model.TvInfo;

public class LinkedDeviceRepository
{
    private static final String TAG = "LinkedDeviceRepository";
    PreferenceManager mPreferenceManager ;
    private Context context;
    private MutableLiveData<List<TvInfo>> liveLinkedDevice = new MutableLiveData<>() ;
    private MutableLiveData<Boolean> removedDeviceStatus = new MutableLiveData<>();

    public LinkedDeviceRepository(Context context)
    {
        mPreferenceManager = new PreferenceManager(context);
        this.context = context;
        fetchLinkedDevicesListFromServer();
    }

    public void fetchLinkedDevicesListFromServer() {

//        ApiClient.getClient(context)
//                .create(MyProfileInterface.class)
//                .getLinkDevices(mPreferenceManager.getGoogleId())




        new Retrofit.Builder()
                .baseUrl("http://192.168.1.143:5081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MyProfileInterface.class)
                .getLinkDevices(mPreferenceManager.getGoogleId())
                .enqueue(new Callback<List<TvInfo>>() {
                    @Override
                    public void onResponse(Call<List<TvInfo>> call, Response<List<TvInfo>> response) {
                        Log.d(TAG, "onResponse: "+response.code());
                        if(response.code() == 200){
                            if(response.body().size() > 0) {
                                Log.d(TAG, "onResponse: got some thing");
                                liveLinkedDevice.postValue(response.body());
                                removedDeviceStatus.postValue(true);
                            }else {
                                Log.d(TAG, "onResponse: not got any");
                                removedDeviceStatus.postValue(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TvInfo>> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
    }

    public MutableLiveData<List<TvInfo>> getLiveLinkedDevice() {
        return liveLinkedDevice;
    }

    public void removedLinkedDevice(TvInfo tvInfo)
    {

//        ApiClient.getClient(context)
//                .create(MyProfileInterface.class)
//                .removeTvDevice(tvInfo, mPreferenceManager.getGoogleId(), tvInfo.getEmac())


        Log.d(TAG, "removedLinkedDevice: "+mPreferenceManager.getGoogleId()+" "+tvInfo.getEmac());

        new Retrofit.Builder()
                .baseUrl("http://192.168.1.143:5081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MyProfileInterface.class)
                .removeTvDevice(tvInfo, mPreferenceManager.getGoogleId(), tvInfo.getEmac())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200)
                        {
                            Log.d(TAG, "onResponse: ");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
        
        
//                .enqueue(new Callback<List<TvInfo>>() {
//                    @Override
//                    public void onResponse(Call<List<TvInfo>> call, Response<List<TvInfo>> response) {
//                        Log.d(TAG, "onResponse: "+response.code());
//                        if(response.code() == 200) {
//                            if(response.body().size() > 0) {
//                                Log.d(TAG, "onResponse: got some thing");
//                                liveLinkedDevice.postValue(response.body());
//                                removedDeviceStatus.postValue(true);
//                            }else {
//                                Log.d(TAG, "onResponse: not got any");
//                                removedDeviceStatus.postValue(false);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<TvInfo>> call, Throwable t) {
//                        Log.d(TAG, "onFailure: "+t.getMessage());
//                        removedDeviceStatus.postValue(false);
//                    }
//                });
    }

    public MutableLiveData<Boolean> getRemovedDeviceStatus(){
        return removedDeviceStatus;
    }
}
