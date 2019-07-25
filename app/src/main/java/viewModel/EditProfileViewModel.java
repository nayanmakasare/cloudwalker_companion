package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import model.NewUserProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.NetworkUtils;

public class EditProfileViewModel extends AndroidViewModel
{
    private static final String TAG = "EditProfileViewModel";
    public MutableLiveData<Boolean> getEditProfileStatus() {
        return editProfileStatus;
    }

    private MutableLiveData<Boolean> editProfileStatus = new MutableLiveData<>();
    private PreferenceManager preferenceManager;

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
    }


    public void editProfileApiCall(NewUserProfile userProfile,  boolean isOtpVerified, Context context)
    {
        if (isOtpVerified && NetworkUtils.getConnectivityStatus(context) > 0) {
            Log.d(TAG, "getResultFromOtp: Bingo");

            new Retrofit.Builder()
                    .baseUrl("http://192.168.1.143:5081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(MyProfileInterface.class)
                    .modifyUserProfile(userProfile, preferenceManager.getGoogleId())
                    .enqueue(new Callback<NewUserProfile>() {
                        @Override
                        public void onResponse(Call<NewUserProfile> call, Response<NewUserProfile> response) {
                            if (response.code() == 200) {
                                preferenceManager.setIsCloudwalkerSignIn(true);
                                editProfileStatus.postValue(true);
                            }else {
                                editProfileStatus.postValue(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<NewUserProfile> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t.getMessage());
                            editProfileStatus.postValue(false);
                        }
                    });
        } else {
            Toast.makeText(context, "Otp verification Process failed. Please check your Internet connection.", Toast.LENGTH_LONG).show();
        }
    }
}
