package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import appUtils.PreferenceManager;
import model.NewUserProfile;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityCwGoogleBinding;

public class CwGoogleActivity extends AppCompatActivity  {

    private ActivityCwGoogleBinding activityCwGoogleBinding;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "CwGoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCwGoogleBinding = DataBindingUtil.setContentView(this, R.layout.activity_cw_google);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        Log.d(TAG, "onCreate: google status "+preferenceManager.getGoogleSignInStatus());
        Log.d(TAG, "onCreate: interm status "+preferenceManager.getCwIntermidiateStatus());
        Log.d(TAG, "onCreate: Pref status "+preferenceManager.getCwPrefrenceStatus());
        getSupportActionBar().hide();
        activityCwGoogleBinding.googleButton.setSize(SignInButton.SIZE_WIDE);
        activityCwGoogleBinding.googleButton.setColorScheme(SignInButton.COLOR_DARK);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.webApplicationClientId))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        activityCwGoogleBinding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            activityCwGoogleBinding.googleButton.setVisibility(View.VISIBLE);
        } else {
            updateUI(account);
        }
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 777) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            e.printStackTrace();
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            PreferenceManager preferenceManager = new PreferenceManager(this);
            preferenceManager.setGoogleSigninStatus(true);
            preferenceManager.setGoogleId(account.getId());

            NewUserProfile newUserProfile = new NewUserProfile();
            newUserProfile.setCwId(account.getId());
            newUserProfile.setUserName(account.getDisplayName());
            newUserProfile.setImageUrl(account.getPhotoUrl().toString());
            newUserProfile.setEmail(account.getEmail());

            Intent intent = new Intent(CwGoogleActivity.this, CwIntermideateActivity.class);
            intent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
            startActivity(intent);
            finish();
        } else {
            activityCwGoogleBinding.signinMessageTV.setText("No success");
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 777);
    }
}
