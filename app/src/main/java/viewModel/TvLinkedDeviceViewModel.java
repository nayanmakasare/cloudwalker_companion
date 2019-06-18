package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import model.NewUserProfile;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import repository.TvLinkedDeviceRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import room.TvInfo;
import utils.NetworkUtils;

public class TvLinkedDeviceViewModel extends AndroidViewModel implements LifecycleObserver
{
    private static final String TAG = "TvLinkedDeviceViewModel";
    private TvLinkedDeviceRepository tvLinkedDeviceRepository;
    private LiveData<List<TvInfo>> allLinkedDevice ;
    private PreferenceManager preferenceManager;

    public MutableLiveData<Boolean> getDeleteDeviceStatus() {
        return deleteDeviceStatus;
    }

    private MutableLiveData<Boolean> deleteDeviceStatus = new MutableLiveData<>();

    public MutableLiveData<List<TvInfo>> getLinkedDevicesLive() {
        return linkedDevicesLive;
    }

    private MutableLiveData<List<TvInfo>> linkedDevicesLive = new MutableLiveData<>();

    public TvLinkedDeviceViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        tvLinkedDeviceRepository = new TvLinkedDeviceRepository(application);
        allLinkedDevice = tvLinkedDeviceRepository.getAllLinkedDevice();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void getAllLinkedDevices() {
        Log.d(TAG, "getAllLinkedDevices: ");
        if (NetworkUtils.getConnectivityStatus(getApplication()) > 0)
        {
            Log.d(TAG, "getAllLinkedDevices: 1 ");
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);

            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.222:5080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MyProfileInterface.class)
                    .getLinkDevices(preferenceManager.getGoogleId())
                    .enqueue(new Callback<List<TvInfo>>() {
                        @Override
                        public void onResponse(Call<List<TvInfo>> call, Response<List<TvInfo>> response) {
                            if(response.code() == 200)
                            {
                                if(response.body().size() > 0){
                                    linkedDevicesLive.postValue(response.body());
                                }else {
                                    linkedDevicesLive.postValue(null);
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<List<TvInfo>> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t.getMessage());
                        }
                    });
        }
    }


    public void deleteLinkDevice(TvInfo tvInfo){

        if (NetworkUtils.getConnectivityStatus(getApplication()) > 0) {
            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.222:5080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(MyProfileInterface.class)
                    .removeTvDevice(tvInfo,  preferenceManager.getGoogleId(), tvInfo.getEmac() )
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(TAG, "onResponse: "+response.code());
                            if(response.code() == 200) {
                                getAllLinkedDevices();
                                deleteDeviceStatus.postValue(true);
                            }
                            else {
                                deleteDeviceStatus.postValue(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            deleteDeviceStatus.postValue(false);
                        }
                    });
        }
        else {
            deleteDeviceStatus.postValue(false);
        }
    }

    public void insert(TvInfo tvInfo) {
        tvLinkedDeviceRepository.insert(tvInfo);
    }

    public void delete(TvInfo tvInfo) {
        Log.d(TAG, "delete: ");
        tvLinkedDeviceRepository.delete(tvInfo);
    }

    public void update(TvInfo tvInfo) {
        tvLinkedDeviceRepository.update(tvInfo);
    }

    public void deleteAllDevice() {
        tvLinkedDeviceRepository.deleteAllDevice();
    }

    public LiveData<List<TvInfo>> getAllLinkedDevice(){
        return allLinkedDevice;
    }
}
