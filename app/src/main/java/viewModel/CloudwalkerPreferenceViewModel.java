package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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

public class CloudwalkerPreferenceViewModel extends AndroidViewModel
{

    private static final String TAG = "CloudwalkerPreferenceVi";
    private PreferenceManager preferenceManager;

    public MutableLiveData<Boolean> getCreateProfileStatus() {
        return createProfileStatus;
    }

    private MutableLiveData<Boolean> createProfileStatus = new MutableLiveData<>();

    public MutableLiveData<Boolean> getBothLoginStatus() {
        return bothLoginStatus;
    }

    private MutableLiveData<Boolean> bothLoginStatus = new MutableLiveData<>();


    public CloudwalkerPreferenceViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
    }




    public void createNewProfile(NewUserProfile userProfile, boolean isOtpVerified , Context context) {
        if (isOtpVerified && NetworkUtils.getConnectivityStatus(context) > 0) {
            Log.d(TAG, "getResultFromOtp: Bingo");

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);


            preferenceManager.setMobileNumber(userProfile.getMobileNumber());
            preferenceManager.setGender(userProfile.getGender());
            preferenceManager.setGenre(TextUtils.join(",", userProfile.getGenre()));
            preferenceManager.setLanguage(TextUtils.join(",", userProfile.getLaunguage()));
            preferenceManager.setType(TextUtils.join(",", userProfile.getContentType()));
            preferenceManager.setDob(userProfile.getDob());



            Log.d(TAG, "getResultFromOtp: genere "+userProfile.getGenre());
            Log.d(TAG, "getResultFromOtp: lang "+userProfile.getLaunguage());
            Log.d(TAG, "getResultFromOtp: content "+userProfile.getContentType());

            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.222:5080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build()
                    .create(MyProfileInterface.class)
                    .postUserProfile(userProfile)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(TAG, "onResponse: " + response.code());
                            if (response.code() == 200) {
                                preferenceManager.setIsCloudwalkerSignIn(true);
                                createProfileStatus.postValue(true);
                                return;
                            }
                            preferenceManager.setIsCloudwalkerSignIn(false);
                            createProfileStatus.postValue(false);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t);
                            preferenceManager.setIsCloudwalkerSignIn(false);
                            createProfileStatus.postValue(false);
                        }
                    });
        } else {
            Toast.makeText(context, "Otp verification Process failed. Please check your Internet connection.", Toast.LENGTH_LONG).show();
            createProfileStatus.postValue(false);
        }
    }

    public void checkIfGoogleAndCloudwalkerLogin()
    {
        if (preferenceManager.isGoogleSignIn() && preferenceManager.isCloudwalkerSigIn()) {
            bothLoginStatus.postValue(true);
        }else
        {
            bothLoginStatus.postValue(false);
        }
    }
}
