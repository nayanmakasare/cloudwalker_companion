package fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import api.ServiceGenerator;
import appUtils.PreferenceManager;
import model.NewUserProfile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.CwGoogleActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.CwTvProfileFragmentLayoutBinding;
import utils.NetworkUtils;

public class CwTvProfileFragment extends Fragment {

    private NewUserProfile newUserProfile;
    private CwTvProfileFragmentLayoutBinding cwTvProfileFragmentLayoutBinding;
    private static final String TAG = "CwTvProfileFragment";
    private View rootView;
    private PreferenceManager preferenceManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        cwTvProfileFragmentLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.cw_tv_profile_fragment_layout, container, false);
        return cwTvProfileFragmentLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        newUserProfile = getActivity().getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
        if(newUserProfile == null) {
            fetchProfileFromServer();
        }else {
            fillProfile();
        }

        cwTvProfileFragmentLayoutBinding.deleteProfileButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if(NetworkUtils.getConnectivityStatus(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED){
                    String googleId = preferenceManager.getGoogleId();
                    Log.d(TAG, "onClick: "+googleId);
                    if(googleId.isEmpty()){
                        clearAndGoToGoogleSignIn(preferenceManager);
                        return;
                    }else {
                        Log.d(TAG, "onClick: deleting ");
                        ServiceGenerator.getRequestApi().deleteProfile(googleId).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d(TAG, "onResponse: "+response.code());
                                if(response.code() == 200) {
                                    Toast.makeText(getActivity(), "Profile Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    clearAndGoToGoogleSignIn(preferenceManager);
                                }else {
                                    Toast.makeText(getActivity(), response.code() +" Internal server error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getActivity(), " Internal server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Not connected to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cwTvProfileFragmentLayoutBinding.editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

            }
        });
    }

    private void fillProfile()
    {
        Glide.with(rootView.getContext()).load(newUserProfile.getImageUrl()).into(cwTvProfileFragmentLayoutBinding.profileCircleIv);
        cwTvProfileFragmentLayoutBinding.userNameProfileEt.setText(newUserProfile.getUserName());
        cwTvProfileFragmentLayoutBinding.emailProfileEt.setText(newUserProfile.getEmail());
        cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.setText(newUserProfile.getMobileNumber());
        StringBuilder text = new StringBuilder();
        for(String s : newUserProfile.getGenre()){
            text.append(s).append(", ");
        }
        cwTvProfileFragmentLayoutBinding.genreCwSpinner.setText(text);
        StringBuilder text1 = new StringBuilder();
        for(String s : newUserProfile.getLaunguage()){
            text1.append(s).append(", ");
        }
        cwTvProfileFragmentLayoutBinding.languageCwSpinner.setText(text1);
        StringBuilder text2 = new StringBuilder();
        for(String s : newUserProfile.getContentType()){
            text2.append(s).append(", ");
        }
        cwTvProfileFragmentLayoutBinding.typeCwSpinner.setText(text2);
    }

    private void fetchProfileFromServer()
    {

        String googleId = preferenceManager.getGoogleId();
        Log.d(TAG, "fetchProfileFromServer: googleId "+googleId);
        if(googleId.isEmpty()){
            clearAndGoToGoogleSignIn(preferenceManager);
            return;
        }
        else {
            if(NetworkUtils.getConnectivityStatus(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED){
                ServiceGenerator.getRequestApi().getUserProfile(googleId).enqueue(new Callback<NewUserProfile>() {
                    @Override
                    public void onResponse(Call<NewUserProfile> call, Response<NewUserProfile> response) {
                        if(response.code() == 200) {
                            newUserProfile = response.body();
                            fillProfile();
                        }
                        else {
                            Toast.makeText(getActivity(), response.code() +" Internal server error", Toast.LENGTH_SHORT).show();
                            clearAndGoToGoogleSignIn(preferenceManager);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewUserProfile> call, Throwable t) {
                        Toast.makeText(getActivity(), " Internal server error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(getActivity(), "Not connected to Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearAndGoToGoogleSignIn(PreferenceManager preferenceManager){
        preferenceManager.setGoogleSigninStatus(false);
        preferenceManager.setCwIntermidiateStatus(false);
        preferenceManager.setCwPrefrenceStatus(false);
        getActivity().startActivity(new Intent(getActivity(), CwGoogleActivity.class));
        getActivity().finish();
    }
}
