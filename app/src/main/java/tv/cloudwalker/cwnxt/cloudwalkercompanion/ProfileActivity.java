package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import appUtils.CustomAdapter;
import databinding.IProfile;
import model.NewUserProfile;
import model.ProfileInfoList;
import model.TvInfo;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityProfileBinding;
import viewModel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity implements IProfile {

    private static final String TAG = "ProfileActivity";
    private ActivityProfileBinding activityProfileBinding;
    private ProfileViewModel profileViewModel;
    private ListView profileInfoList ;
    private NewUserProfile currentUserProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        activityProfileBinding.setIProfile(this);
        getSupportActionBar().hide();
        profileInfoList = findViewById(R.id.profileInfoList);


        // viewModel
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        getLifecycle().addObserver(profileViewModel);


        profileViewModel.getNewUserProfileMutableLiveData().observe(this, new Observer<NewUserProfile>() {
            @Override
            public void onChanged(@Nullable NewUserProfile newUserProfile) {
                if(newUserProfile != null) {
                    Log.d(TAG, "onChanged: genre "+newUserProfile.getGenre().size());
                    Log.d(TAG, "onChanged: lang "+newUserProfile.getLaunguage().size());
                    Log.d(TAG, "onChanged: content "+newUserProfile.getContentType().size());
                    currentUserProfile = newUserProfile;
                    settingDataToUi(newUserProfile);
                }
            }
        });

        profileViewModel.getFetchProfileStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null && !aBoolean) {
                    Toast.makeText(ProfileActivity.this, "Problem while getting your profile. Please check internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileViewModel.getDeleteProfileResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) {
                    if(aBoolean) {
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Log.d(TAG, "onChanged: Profile not able to deleted Please Check your Internet Connection ");
                    }
                }
            }
        });
    }


    private void settingDataToUi(NewUserProfile newUserProfile){
        activityProfileBinding.setNewUserProfile(newUserProfile);
        List<ProfileInfoList> profileInfoLists = new ArrayList<>();
        profileInfoLists.add(new ProfileInfoList(newUserProfile.getEmail(), R.drawable.ic_email_black_24dp));
        profileInfoLists.add(new ProfileInfoList(newUserProfile.getMobileNumber(), R.drawable.ic_phone_android_black_24dp));
        profileInfoLists.add(new ProfileInfoList(newUserProfile.getGender(), R.drawable.ic_group_add_black_24dp));
        profileInfoLists.add(new ProfileInfoList(newUserProfile.getDob(), R.drawable.ic_date_range_black_24dp));

        List<TvInfo> tvInfoList = newUserProfile.getLinkedDevices();
        if(tvInfoList != null &&tvInfoList.size() > 0) {
            for(TvInfo tvInfo : tvInfoList)
                profileInfoLists.add(new ProfileInfoList("CloudTv_"+tvInfo.getEmac(), R.drawable.ic_tv_blue_24dp));
        }
        profileInfoList.setAdapter(new CustomAdapter(ProfileActivity.this, profileInfoLists));
    }

    public void deleteProfile(final View view) {
        Log.d(TAG, "deleteProfile: ");
        profileViewModel.deleteProfile(view.getContext());
    }

    @Override
    public void inflateEditProfileDialog() {
        if(currentUserProfile != null){
            Intent editIntent = new Intent(this, EditProfileActivity.class);
            editIntent.putExtra(NewUserProfile.class.getSimpleName(), currentUserProfile);
            Log.d(TAG, "inflateEditProfileDialog: genre "+currentUserProfile.getGenre().size());
            Log.d(TAG, "inflateEditProfileDialog: language "+currentUserProfile.getLaunguage().size());
            Log.d(TAG, "inflateEditProfileDialog: content "+currentUserProfile.getContentType().size());
            Log.d(TAG, "inflateEditProfileDialog: gender "+currentUserProfile.getGender());
            Log.d(TAG, "inflateEditProfileDialog: linkedDevices "+currentUserProfile.getLinkedDevices().size());
            startActivity(editIntent);
        }
    }
}
