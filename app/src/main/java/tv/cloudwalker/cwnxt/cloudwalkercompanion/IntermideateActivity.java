package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import de.hdodenhof.circleimageview.CircleImageView;
import model.NewUserProfile;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IntermideateActivity extends AppCompatActivity
{
    private static final String TAG = "IntermideateActivity";
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermideate);
        preferenceManager = new PreferenceManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(preferenceManager.isGoogleSignIn() && preferenceManager.isCloudwalkerSigIn()){
            startActivity(new Intent(IntermideateActivity.this, MainActivity.class));
            onBackPressed();
        }
        else if(preferenceManager.isGoogleSignIn() && !preferenceManager.isCloudwalkerSigIn()) {
            NewUserProfile newUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
            Log.d(TAG, "onStart: "+newUserProfile);
            checkIfUserAlreadyPresent(newUserProfile);
        }else {
            ((EditText)findViewById(R.id.fullname)).setText(preferenceManager.getUserName());
            ((EditText)findViewById(R.id.useremail)).setText(preferenceManager.getUserEmail());
            ((EditText)findViewById(R.id.usergoogleId)).setText(preferenceManager.getGoogleId());
            Glide.with(this).load(preferenceManager.getProfileImageUrl()).into((CircleImageView)findViewById(R.id.profile_image));
        }
    }

    public void createPreference(View view) {
        startActivity(new Intent(view.getContext(), CloudwalkerPreferenceActivity.class));
        onBackPressed();
    }

    public void checkIfUserAlreadyPresent(final NewUserProfile newUserProfile)
    {
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
                .getUserProfile(newUserProfile.getCwId())
                .enqueue(new Callback<NewUserProfile>() {
                    @Override
                    public void onResponse(Call<NewUserProfile> call, Response<NewUserProfile> response) {
                        if(response.code() == 200) {
                            preferenceManager.setIsCloudwalkerSignIn(true);
                            Intent intent = new Intent(IntermideateActivity.this, MainActivity.class);
                            intent.putExtra(NewUserProfile.class.getSimpleName(), response.body());
                            startActivity(intent);
                            onBackPressed();
                        }
                        else {
                            Intent prefrenceIntent = new Intent(IntermideateActivity.this, CloudwalkerPreferenceActivity.class);
                            prefrenceIntent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
                            startActivity(prefrenceIntent);
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onFailure(Call<NewUserProfile> call, Throwable t) {

                    }
                });
    }
}







































//
//    private static final String CLIENT_ID = "953591453580-13ecc9eds039d78l8o5p8q6egtndoigu.apps.googleusercontent.com";
//
//    private static final String REDIRECT_URI = "tv.cloudwalker.cwnxt.cloudwalkercompanion:/oauth2redirect";
//    private static final String REDIRECT_URI_ROOT = "tv.cloudwalker.cwnxt.cloudwalkercompanion";
//    private static final String CODE = "code";
//    private static final String ERROR_CODE = "error";
//    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
//    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
//    public static final String API_SCOPE = "https://www.googleapis.com/auth/youtube.readonly";
//    private String code;
//    private String error;
//    private static final String TAG = "IntermideateActivity";
//
//
//
//
//    private void initiateOAuth()
//    {
//        Uri data = getIntent().getData();
//        if (data != null && !TextUtils.isEmpty(data.getScheme())) {
//            if (REDIRECT_URI_ROOT.equals(data.getScheme())) {
//                code = data.getQueryParameter(CODE);
//                error=data.getQueryParameter(ERROR_CODE);
//                Log.e(TAG, "onCreate: handle result of authorization with code :" + code);
//                if (!TextUtils.isEmpty(code)) {
//                    getTokenFormUrl();
//                }
//                if(!TextUtils.isEmpty(error)) {
//                    Toast.makeText(this, R.string.loginactivity_grantsfails_quit,Toast.LENGTH_LONG).show();
//                    Log.e(TAG, "onCreate: handle result of authorization with error :" + error);
//                    finish();
//                }
//            }
//        } else {
//            OAuthToken oauthToken=OAuthToken.Factory.create(IntermideateActivity.this);
//            if (oauthToken==null
//                    ||oauthToken.getAccessToken()==null) {
//                if(oauthToken==null||oauthToken.getRefreshToken()==null){
//                    Log.e(TAG, "onCreate: Launching authorization (first step)");
//                    makeAuthorizationRequest();
//                }else{
//                    Log.e(TAG, "onCreate: refreshing the token :" + oauthToken);
//                    refreshTokenFormUrl(oauthToken);
//                }
//            }
//            else {
//                Log.e(TAG, "onCreate: Token available, just launch MainActivity");
//                startMainActivity(false);
//            }
//        }
//    }
//
//    private void makeAuthorizationRequest() {
//        HttpUrl authorizeUrl = HttpUrl.parse("https://accounts.google.com/o/oauth2/v2/auth") //
//                .newBuilder() //
//                .addQueryParameter("client_id", CLIENT_ID)
//                .addQueryParameter("scope", API_SCOPE)
//                .addQueryParameter("redirect_uri", REDIRECT_URI)
//                .addQueryParameter("response_type", CODE)
//                .build();
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        Log.e(TAG, "the url is : " + String.valueOf(authorizeUrl.url()));
//        i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
//        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
//        finish();
//    }
//
//    private void refreshTokenFormUrl(OAuthToken oauthToken)
//    {
//        String BASE_URL = "https://www.googleapis.com/";
//
//        new Retrofit.Builder().baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(MyProfileInterface.class).refreshTokenForm(
//                oauthToken.getRefreshToken(),
//                CLIENT_ID,
//                GRANT_TYPE_REFRESH_TOKEN
//        )
//                .enqueue(new Callback<OAuthToken>() {
//                    @Override
//                    public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
//                        Log.e(TAG, "===============New Call==========================");
//                        Log.e(TAG, "The call refreshTokenFormUrl succeed with code=" + response.code() + " and has body = " + response.body());
//                        //ok we have the token
//                        response.body().save(IntermideateActivity.this);
//                        startMainActivity(true);
//                    }
//
//                    @Override
//                    public void onFailure(Call<OAuthToken> call, Throwable t) {
//                        Log.e(TAG, "===============New Call==========================");
//                        Log.e(TAG, "The call refreshTokenFormCall failed", t);
//                    }
//                });
//    }
//
//    /**
//     * Retrieve the OAuth token
//     */
//    private void getTokenFormUrl() {
//        String BASE_URL = "https://www.googleapis.com/";
//
//        new Retrofit.Builder().baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(MyProfileInterface.class)
//                .requestTokenForm(
//                        code,
//                        CLIENT_ID,
//                        REDIRECT_URI,
//                        GRANT_TYPE_AUTHORIZATION_CODE
//                ).enqueue(new Callback<OAuthToken>() {
//            @Override
//            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
//                Log.e(TAG, "===============New Call==========================");
//                Log.e(TAG, "The call getRequestTokenFormCall succeed with code=" + response.code() + " and has body = " + response.body());
//                //ok we have the token
//                response.body().save(IntermideateActivity.this);
//                startMainActivity(true);
//            }
//
//            @Override
//            public void onFailure(Call<OAuthToken> call, Throwable t) {
//                Log.e(TAG, "===============New Call==========================");
//                Log.e(TAG, "The call getRequestTokenFormCall failed", t);
//
//            }
//        });
//    }
//
//    /**
//     * Start the next activity
//     */
//    private void startMainActivity(boolean newtask) {
//        NewUserProfile newUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
//        Intent prefrenceIntent = new Intent(IntermideateActivity.this, CloudwalkerPreferenceActivity.class);
//        prefrenceIntent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
//        startActivity(prefrenceIntent);
//        onBackPressed();
//    }
