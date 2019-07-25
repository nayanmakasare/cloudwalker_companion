package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import model.NewUserProfile;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.NetworkUtils;

public class ProfileViewModel extends AndroidViewModel implements LifecycleObserver
{

    private MutableLiveData<NewUserProfile> newUserProfileMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> fetchProfileStatus = new MutableLiveData<>();
    private PreferenceManager preferenceManager;
    private static final String TAG = "ProfileViewModel";

    private MutableLiveData<Boolean> deleteProfileResult = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application)
    {
        super(application);
        Log.d(TAG, "ProfileViewModel: const");
        preferenceManager = new PreferenceManager(application);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void getProfile()
    {
        Log.d(TAG, "getProfile: ");
        if(NetworkUtils.getConnectivityStatus(getApplication()) != NetworkUtils.TYPE_NOT_CONNECTED) {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);

            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.143:5081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build()
                    .create(MyProfileInterface.class)
                    .getUserProfile(preferenceManager.getGoogleId())
                    .enqueue(new Callback<NewUserProfile>() {
                        @Override
                        public void onResponse(Call<NewUserProfile> call, Response<NewUserProfile> response) {
                            if(response.code() == 200) {
                                Log.d(TAG, "onResponse: 200 ");
                                newUserProfileMutableLiveData.postValue(response.body());
                                fetchProfileStatus.postValue(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<NewUserProfile> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t.getMessage());
                            fetchProfileStatus.postValue(false);
                        }
                    });
        }
        else {
            fetchProfileStatus.postValue(false);
        }
    }

    public MutableLiveData<Boolean> getFetchProfileStatus() {
        return fetchProfileStatus;
    }

    public MutableLiveData<NewUserProfile> getNewUserProfileMutableLiveData(){
        return newUserProfileMutableLiveData;
    }


    public void deleteProfile(Context context)
    {
        Log.d(TAG, "deleteProfile: ");
        if (NetworkUtils.getConnectivityStatus(context) > 0) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);

            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.143:5081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build().create(MyProfileInterface.class)
                    .deleteProfile(preferenceManager.getGoogleId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code() == 200) {
                                preferenceManager.setIsGoogleSignIn(false);
                                preferenceManager.setIsCloudwalkerSignIn(false);
                                preferenceManager.setGoogleId("");
                                preferenceManager.setLinkedNsdDevices(null);
                                preferenceManager.setDob("");
                                preferenceManager.setGender("");
                                preferenceManager.setLanguage("");
                                preferenceManager.setMobileNumber("");
                                preferenceManager.setProfileImageUrl("");
                                preferenceManager.setUserEmail("");
                                preferenceManager.setUserName("");
                                preferenceManager.setTvInfo("");
                                deleteProfileResult.postValue(true);
                            }
                            else {
                                deleteProfileResult.postValue(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t.getMessage());
                            deleteProfileResult.postValue(false);
                        }
                    });
        }
        else
        {
            deleteProfileResult.postValue(false);
        }


    }

    public MutableLiveData<Boolean> getDeleteProfileResult() {
        return deleteProfileResult;
    }
}
