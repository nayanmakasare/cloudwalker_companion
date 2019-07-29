package fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
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
import java.util.List;
import java.util.Set;

import adapter.CwLinkedDevicesAdapter;
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
import utils.AppUtils;
import utils.NetworkUtils;

public class CwTvProfileFragment extends Fragment {

    private NewUserProfile newUserProfile;
    private CwTvProfileFragmentLayoutBinding cwTvProfileFragmentLayoutBinding;
    private static final String TAG = "CwTvProfileFragment";
    private View rootView;
    private PreferenceManager preferenceManager;
    private CwLinkedDevicesAdapter cwLinkedDevicesAdapter = new CwLinkedDevicesAdapter();


    private List<String> genreArrayList;
    private List<String> languageArrayList;
    private List<String> typeArrayList;
    private String[] genreList, languageList, contentList;
    private boolean[] genreCheckedItemArray, languageCheckedItemArray, contentCheckedItemArray ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getActivity());
        Set<String> linkedDevice = preferenceManager.getLinkedNsdDevices();
        if(linkedDevice != null){
            for(String s : linkedDevice){
                Log.d(TAG, "onCreate: linked devices "+s);
            }
        }

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
        rootView = view;
        newUserProfile = getActivity().getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
        hideSoftKeyboard();
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


        cwTvProfileFragmentLayoutBinding.genreCwSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
                    NewUserProfile newUserProfile = new NewUserProfile();
                    newUserProfile.setUserName(cwTvProfileFragmentLayoutBinding.userNameProfileEt.getText().toString());
                    newUserProfile.setEmail(cwTvProfileFragmentLayoutBinding.emailProfileEt.getText().toString());
                    newUserProfile.setMobileNumber(cwTvProfileFragmentLayoutBinding.phoneNumberProfileET.getText().toString());
                }
            }
        });

        cwLinkedDevicesAdapter.setOnItemClickedListener(new CwLinkedDevicesAdapter.OnItemClickListener() {
            @Override
            public void onDeviceClicked(String nsdServiceInfo) {

            }

            @Override
            public void onDeleteLinkedClicked(String nsdServiceInfo) {
                    deleteDevice(nsdServiceInfo);
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

    private void deleteDevice(String deviceName)
    {
        if(NetworkUtils.getConnectivityStatus(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED){
            //TODO need to implement
        }
        else
        {
            Toast.makeText(getActivity(), "Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }


    private void fillProfile() {
        Glide.with(rootView.getContext()).load(newUserProfile.getImageUrl()).into(cwTvProfileFragmentLayoutBinding.profileCircleIv);
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
        cwTvProfileFragmentLayoutBinding.linkedDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        cwTvProfileFragmentLayoutBinding.linkedDeviceRecyclerView.setAdapter(cwLinkedDevicesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        Set<String> linkedDevice = preferenceManager.getLinkedNsdDevices();
        if(linkedDevice != null){
            cwLinkedDevicesAdapter.submitList(new ArrayList<>(linkedDevice));
        }
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
