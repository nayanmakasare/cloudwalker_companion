package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import api.ServiceGenerator;
import appUtils.PreferenceManager;
import model.NewUserProfile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityCwPreferenceBinding;
import utils.AppUtils;
import utils.NetworkUtils;

public class CwPreferenceActivity extends AppCompatActivity {


    private ActivityCwPreferenceBinding activityCwPreferenceBinding ;
    private NewUserProfile newUserProfile;
    private List<String> genreArrayList = new ArrayList<>();
    private List<String> languageArrayList = new ArrayList<>();
    private List<String> typeArrayList = new ArrayList<>();
    private static final String TAG = "CwPreferenceActivity";
    private String[] genreList, languageList, contentList;
    private boolean[] genreCheckedItemArray, languageCheckedItemArray, contentCheckedItemArray ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCwPreferenceBinding = DataBindingUtil.setContentView(this, R.layout.activity_cw_preference);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        Log.d(TAG, "onCreate: google status "+preferenceManager.getGoogleSignInStatus());
        Log.d(TAG, "onCreate: interm status "+preferenceManager.getCwIntermidiateStatus());
        Log.d(TAG, "onCreate: Pref status "+preferenceManager.getCwPrefrenceStatus());
        Log.d(TAG, "onCreate: Google id "+preferenceManager.getGoogleId());

        getSupportActionBar().hide();
        newUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
        if(newUserProfile == null){
            fetchProfileFromServer();
        }

        try {
            JSONObject profileConfigObj = new JSONObject(AppUtils.loadJSONFromAsset(this));
            genreList = getStringArrayFromJSON(profileConfigObj.getJSONArray("genres"));
            genreCheckedItemArray = new boolean[genreList.length];
            languageList = getStringArrayFromJSON(profileConfigObj.getJSONArray("languages"));
            languageCheckedItemArray = new boolean[languageList.length];
            contentList = getStringArrayFromJSON(profileConfigObj.getJSONArray("content_type"));
            contentCheckedItemArray = new boolean[contentList.length];
        } catch (JSONException e) {
            e.printStackTrace();
        }


        activityCwPreferenceBinding.genreCwSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
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


        activityCwPreferenceBinding.languageCwSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
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


        activityCwPreferenceBinding.typeCwSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
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


        activityCwPreferenceBinding.createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreArrayList.size() > 0 &&
                        languageArrayList.size() > 0 &&
                        typeArrayList.size() > 0 )
                {
                    newUserProfile.setGenre(genreArrayList);
                    newUserProfile.setLaunguage(languageArrayList);
                    newUserProfile.setContentType(typeArrayList);
                    registerNewUser(v.getContext());
                }
                else {
                    Toast.makeText(CwPreferenceActivity.this, "Please select all the prefrence", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerNewUser(final Context context)
    {
        if(NetworkUtils.getConnectivityStatus(context) > 0) {
            ServiceGenerator.getRequestApi().postUserProfile(newUserProfile).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: "+response.code());
                    if(response.code() == 200) {
                        newUserProfile.setGenre(genreArrayList);
                        newUserProfile.setLaunguage(languageArrayList);
                        newUserProfile.setContentType(typeArrayList);
                        Intent intent = new Intent(context, PrimeActivity.class);
                        intent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
                        PreferenceManager preferenceManager = new PreferenceManager(context);
                        preferenceManager.setCwPrefrenceStatus(true);
                        preferenceManager.setCwIntermidiateStatus(true);
                        preferenceManager.setGoogleSigninStatus(true);
                        startActivity(intent);
                        finish();
                    }
                    else if(response.code() == 201){
                        Toast.makeText(CwPreferenceActivity.this, "Profile Found on Server. Fetching your profile.", Toast.LENGTH_SHORT).show();
                        fetchProfileFromServer();
                    }else {
                        Toast.makeText(CwPreferenceActivity.this, "Internal Server Error.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "onFailure: "+t.getMessage());
                    Toast.makeText(CwPreferenceActivity.this, "Internal Server Error.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(CwPreferenceActivity.this, "Not connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchProfileFromServer()
    {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String googleId = preferenceManager.getGoogleId();
        Log.d(TAG, "fetchProfileFromServer: googleId "+googleId);
        if(googleId.isEmpty()){
            clearAndGoToGoogleSignIn(preferenceManager);
            return;
        }
        else {
            if(NetworkUtils.getConnectivityStatus(this) != NetworkUtils.TYPE_NOT_CONNECTED){
                ServiceGenerator.getRequestApi().getUserProfile(googleId).enqueue(new Callback<NewUserProfile>() {
                    @Override
                    public void onResponse(Call<NewUserProfile> call, Response<NewUserProfile> response) {
                        if(response.code() == 200) {
                            newUserProfile = response.body();
                            newUserProfile.setGenre(genreArrayList);
                            newUserProfile.setLaunguage(languageArrayList);
                            newUserProfile.setContentType(typeArrayList);
                            Intent intent = new Intent(CwPreferenceActivity.this, PrimeActivity.class);
                            intent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
                            PreferenceManager preferenceManager = new PreferenceManager(CwPreferenceActivity.this);
                            preferenceManager.setCwPrefrenceStatus(true);
                            preferenceManager.setCwIntermidiateStatus(true);
                            preferenceManager.setGoogleSigninStatus(true);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            clearAndGoToGoogleSignIn(new PreferenceManager(CwPreferenceActivity.this));
                            Toast.makeText(CwPreferenceActivity.this, response.code() +" Internal server error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<NewUserProfile> call, Throwable t) {
                        Toast.makeText(CwPreferenceActivity.this, " Internal server error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(this, "Not connected to Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearAndGoToGoogleSignIn(PreferenceManager preferenceManager){
        preferenceManager.setGoogleSigninStatus(false);
        preferenceManager.setCwIntermidiateStatus(false);
        preferenceManager.setCwPrefrenceStatus(false);
        startActivity(new Intent(this, CwGoogleActivity.class));
        finish();
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
