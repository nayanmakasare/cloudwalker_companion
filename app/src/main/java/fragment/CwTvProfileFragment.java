package fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapter.CwLinkedDevicesAdapter;
import api.ServiceGenerator;
import appUtils.PreferenceManager;
import model.NewUserProfile;
import model.TvInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.CwGoogleActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.CwNsdListActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.CwTvProfileFragmentLayoutBinding;
import utils.AppUtils;
import utils.NetworkUtils;

public class CwTvProfileFragment extends Fragment {

    private static final String TAG = "CwTvProfileFragment";
    private NewUserProfile newUserProfile;
    private CwTvProfileFragmentLayoutBinding cwTvProfileFragmentLayoutBinding;
    private PreferenceManager preferenceManager;
    private CwLinkedDevicesAdapter cwLinkedDevicesAdapter = new CwLinkedDevicesAdapter();
    private NewUserProfile modifiedNewUserProfile = new NewUserProfile();
    private boolean isProfileEdited = false;
    private List<TvInfo> currentLinkedDevices = null;


    private List<String> genreArrayList;
    private List<String> languageArrayList;
    private List<String> typeArrayList;
    private String[] genreList, languageList, contentList;
    private boolean[] genreCheckedItemArray, languageCheckedItemArray, contentCheckedItemArray ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getActivity());

        try {
            JSONObject profileConfigObj = new JSONObject(AppUtils.loadJSONFromAsset(getActivity()));
            genreList = getStringArrayFromJSON(profileConfigObj.getJSONArray("genres"));
            genreCheckedItemArray = new boolean[genreList.length];
            languageList = getStringArrayFromJSON(profileConfigObj.getJSONArray("languages"));
            languageCheckedItemArray = new boolean[languageList.length];
            contentList = getStringArrayFromJSON(profileConfigObj.getJSONArray("content_type"));
            contentCheckedItemArray = new boolean[contentList.length];
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        settingsTextWatchers();

        cwTvProfileFragmentLayoutBinding.linkedDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        cwTvProfileFragmentLayoutBinding.linkedDeviceRecyclerView.setAdapter(cwLinkedDevicesAdapter);

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


        cwTvProfileFragmentLayoutBinding.genreCwSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideSoftKeyboard();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                genreArrayList = new ArrayList<>();
                builder.setTitle("Select Genre");
                builder.setMultiChoiceItems(genreList, genreCheckedItemArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked) {
                            if(!genreArrayList.contains(genreList[position])) {
                                genreArrayList.add(genreList[position]);
                            }
                        }
                        else
                        {
                            if(genreArrayList.contains(genreList[position])){
                                genreArrayList.remove(genreList[position]);
                            }
                        }
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder text = new StringBuilder();
                        for(String s : genreArrayList){
                            text.append(s).append(", ");
                        }
                        ((EditText)v).setText(text.toString());
                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        cwTvProfileFragmentLayoutBinding.languageCwSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideSoftKeyboard();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                languageArrayList = new ArrayList<>();
                builder.setTitle("Select Genre");
                builder.setMultiChoiceItems(languageList, languageCheckedItemArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked) {
                            if(!languageArrayList.contains(languageList[position])) {
                                languageArrayList.add(languageList[position]);
                            }
                        }
                        else
                        {
                            if(languageArrayList.contains(languageList[position])){
                                languageArrayList.remove(languageList[position]);
                            }
                        }
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder text = new StringBuilder();
                        for(String s : languageArrayList){
                            text.append(s).append(", ");
                        }
                        ((EditText)v).setText(text.toString());
                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        cwTvProfileFragmentLayoutBinding.typeCwSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideSoftKeyboard();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                typeArrayList = new ArrayList<>();
                builder.setTitle("Select Genre");
                builder.setMultiChoiceItems(contentList, contentCheckedItemArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked) {
                            if(!typeArrayList.contains(contentList[position])) {
                                typeArrayList.add(contentList[position]);
                            }
                        }
                        else
                        {
                            if(typeArrayList.contains(contentList[position])){
                                typeArrayList.remove(contentList[position]);
                            }
                        }
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder text = new StringBuilder();
                        for(String s : typeArrayList){
                            text.append(s).append(", ");
                        }
                        ((EditText)v).setText(text.toString());
                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        cwTvProfileFragmentLayoutBinding.editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                if(cwTvProfileFragmentLayoutBinding.userNameProfileEt.getText().length() < 4){
                    Toast.makeText(getActivity(), "UserName should be greater than 4 characters.", Toast.LENGTH_SHORT).show();
                }else if(cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.getText().length() == 0 ){
                    Toast.makeText(getActivity(), "Please enter your mobile number.", Toast.LENGTH_SHORT).show();
                }else if( cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.getText().length() != 10 ) {
                    Toast.makeText(getActivity(), "Invalid Mobile Number. Should contain 10 digits only.", Toast.LENGTH_SHORT).show();
                }else if(cwTvProfileFragmentLayoutBinding.genreCwSpinner.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please enter your genre preference", Toast.LENGTH_SHORT).show();
                }else if(cwTvProfileFragmentLayoutBinding.languageCwSpinner.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please enter your language preference", Toast.LENGTH_SHORT).show();
                }else if(cwTvProfileFragmentLayoutBinding.typeCwSpinner.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please enter your content type preference", Toast.LENGTH_SHORT).show();
                }else {
                    modifiedNewUserProfile.setImageUrl(newUserProfile.getImageUrl());
                    modifiedNewUserProfile.setEmail(cwTvProfileFragmentLayoutBinding.emailProfileEt.getText().toString());
                    modifiedNewUserProfile.setDob(newUserProfile.getDob());
                    modifiedNewUserProfile.setCwId(newUserProfile.getCwId());
                    if(currentLinkedDevices != null || currentLinkedDevices.size() > 0){
                        modifiedNewUserProfile.setLinkedDevices(currentLinkedDevices);
                    }else {
                        modifiedNewUserProfile.setLinkedDevices(new ArrayList<TvInfo>());
                    }
                    editProfile();
                }
            }
        });

        cwLinkedDevicesAdapter.setOnItemClickedListener(new CwLinkedDevicesAdapter.OnItemClickListener() {
            @Override
            public void onDeviceClicked(String nsdServiceInfo) {

            }

            @Override
            public void onDeleteLinkedClicked(String nsdServiceInfo) {
                Log.d(TAG, "onDeleteLinkedClicked: "+nsdServiceInfo);
                deleteDevice(nsdServiceInfo);
            }
        });
    }

    private void settingsTextWatchers()
    {
        cwTvProfileFragmentLayoutBinding.userNameProfileEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isProfileEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    modifiedNewUserProfile.setUserName(s.toString());
                }else {
                    Toast.makeText(getActivity(), "User name can't be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isProfileEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    modifiedNewUserProfile.setMobileNumber(s.toString());
                }else {
                    Toast.makeText(getActivity(), "Mobile Number can't be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cwTvProfileFragmentLayoutBinding.genreCwSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: "+s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isProfileEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    modifiedNewUserProfile.setGenre(genreArrayList);
                }else {
                    Toast.makeText(getActivity(), "Genre prefrence can't be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cwTvProfileFragmentLayoutBinding.languageCwSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isProfileEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    modifiedNewUserProfile.setLaunguage(languageArrayList);
                }else {
                    Toast.makeText(getActivity(), "Language prefrence can't be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cwTvProfileFragmentLayoutBinding.typeCwSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isProfileEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    modifiedNewUserProfile.setContentType(typeArrayList);
                }else {
                    Toast.makeText(getActivity(), "Content Type prefrence can't be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hideSoftKeyboard() {
        cwTvProfileFragmentLayoutBinding.genreCwSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.getWindowToken(), 0);
                return false;
            }
        });
        cwTvProfileFragmentLayoutBinding.languageCwSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.getWindowToken(), 0);
                return false;
            }
        });
        cwTvProfileFragmentLayoutBinding.typeCwSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.getWindowToken(), 0);
                return false;
            }
        });
    }


    private void editProfile()
    {
        if(NetworkUtils.getConnectivityStatus(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED && isProfileEdited){
            //TODO need to implement

            ServiceGenerator.getRequestApi()
                    .modifyUserProfile(modifiedNewUserProfile, preferenceManager.getGoogleId())
                    .enqueue(new Callback<NewUserProfile>() {
                        @Override
                        public void onResponse(Call<NewUserProfile> call, Response<NewUserProfile> response) {
                            Log.d(TAG, "onResponse: "+response.code());
                            if(response.code() == 200){
                                newUserProfile = modifiedNewUserProfile;
                                fillProfile();
                                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<NewUserProfile> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t.getMessage());
                            Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDevice(final String tvEmac)
    {
        if(NetworkUtils.getConnectivityStatus(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED){
            //TODO need to implement
            Log.d(TAG, "deleteDevice: emac "+tvEmac);
            Log.d(TAG, "deleteDevice: prefr current service " + preferenceManager.getCurrentNsdServiceConnected());
            TvInfo tvInfo = new TvInfo(tvEmac, "", "" );
            ServiceGenerator.getRequestApi()
                    .removeTvDevice(tvInfo, preferenceManager.getGoogleId(),tvEmac)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(TAG, "onResponse: "+response.code());
                            if(response.code() == 200){
                                Log.d(TAG, "onResponse: "+preferenceManager.getCurrentNsdServiceConnected() +" "+tvEmac);
                                if(preferenceManager.getCurrentNsdServiceConnected().contains(tvEmac)){
                                    preferenceManager.setCurrentNsdServiceConnected("");
                                    preferenceManager.setNsdPort(0);
                                    preferenceManager.setNsdHostAddress("");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "Deleted currently connected device on the network.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                fetchNewLinkedDevices();
                            }else {
                                Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t.getMessage());
                            Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchNewLinkedDevices()
    {
        if(NetworkUtils.getConnectivityStatus(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED){
            ServiceGenerator.getRequestApi().getLinkDevices(preferenceManager.getGoogleId()).enqueue(new Callback<List<TvInfo>>() {
                @Override
                public void onResponse(Call<List<TvInfo>> call, Response<List<TvInfo>> response) {
                    if(response.code() == 200){
                        if(response.body() != null && response.body().size() > 0){
                            currentLinkedDevices = response.body();
                            settingLinkedDevicesToRecyclerView();
                            Set<String> linkedDevicesSet = new HashSet<>();
                            for(TvInfo tvInfo : currentLinkedDevices){
                                linkedDevicesSet.add(tvInfo.getEmac());
                            }
                            preferenceManager.setLinkedNsdDevices(linkedDevicesSet);
                        }else {
                            Toast.makeText(getActivity(), "O device linked to your profile.", Toast.LENGTH_SHORT).show();
                            startActivityForResult(new Intent(getActivity(), CwNsdListActivity.class), 700);
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<TvInfo>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getActivity(), "Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }


    private void fillProfile() {
        Glide.with(getActivity()).load(newUserProfile.getImageUrl()).into(cwTvProfileFragmentLayoutBinding.profileCircleIv);
        cwTvProfileFragmentLayoutBinding.userNameProfileEt.setText(newUserProfile.getUserName());
        cwTvProfileFragmentLayoutBinding.emailProfileEt.setText(newUserProfile.getEmail());
        cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.setText(newUserProfile.getMobileNumber());
        StringBuilder text = new StringBuilder();
        genreArrayList = newUserProfile.getGenre();
        for(String s : newUserProfile.getGenre()){
            text.append(s).append(", ");
        }
        cwTvProfileFragmentLayoutBinding.genreCwSpinner.setText(text);

        StringBuilder text1 = new StringBuilder();
        languageArrayList = newUserProfile.getLaunguage();
        for(String s : newUserProfile.getLaunguage()){
            text1.append(s).append(", ");
        }
        cwTvProfileFragmentLayoutBinding.languageCwSpinner.setText(text1);

        StringBuilder text2 = new StringBuilder();
        typeArrayList = newUserProfile.getContentType();
        for(String s : newUserProfile.getContentType()){
            text2.append(s).append(", ");
        }
        cwTvProfileFragmentLayoutBinding.typeCwSpinner.setText(text2);


        Log.d(TAG, "fillProfile: linked Devices "+newUserProfile.getLinkedDevices());
        if(newUserProfile.getLinkedDevices() != null && newUserProfile.getLinkedDevices().size() > 0){
            currentLinkedDevices = newUserProfile.getLinkedDevices();
            settingLinkedDevicesToRecyclerView();
        }else {
            preferenceManager.setLinkedNsdDevices(null);
            startActivityForResult(new Intent(getActivity(), CwNsdListActivity.class), 700);
        }
    }


    private void settingLinkedDevicesToRecyclerView(){
        List<String> linkedDevice = new ArrayList<>();
        for(TvInfo tvInfo : currentLinkedDevices){
            linkedDevice.add(tvInfo.getEmac());
        }
        cwLinkedDevicesAdapter.submitList(linkedDevice);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProfileFromServer();
    }

    private void fetchProfileFromServer() {
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

    private String[] getStringArrayFromJSON(JSONArray jsonArray) {
        String[] arrayList = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList[i] = jsonArray.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}
