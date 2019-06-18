package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import appUtils.PreferenceManager;
import model.NewUserProfile;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = SignInActivity.class.getSimpleName();
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.webApplicationClientId))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d(TAG, "onStart: " + account);
        if (account == null) {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
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
            ((TextView) findViewById(R.id.signInmessage)).setText("Loging in.");
            Log.d(TAG, "updateUI: " + account.getId());
            PreferenceManager preferenceManager = new PreferenceManager(SignInActivity.this);
            preferenceManager.setUserEmail(account.getEmail());
            preferenceManager.setUserName(account.getDisplayName());
            preferenceManager.setGoogleId(account.getId());
            preferenceManager.setIsGoogleSignIn(true);
            preferenceManager.setProfileImageUrl(account.getPhotoUrl().toString());

            NewUserProfile newUserProfile = new NewUserProfile();
            newUserProfile.setCwId(account.getId());
            newUserProfile.setUserName(account.getDisplayName());
            newUserProfile.setImageUrl(account.getPhotoUrl().toString());
            newUserProfile.setEmail(account.getEmail());

            Intent intent = new Intent(SignInActivity.this, IntermideateActivity.class);
            intent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
            startActivity(intent);
            onBackPressed();
        } else {
            ((TextView) findViewById(R.id.signInmessage)).setText("No success");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            default:
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //On Succesfull signout we navigate the user back to LoginActivity
                System.exit(0);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 777);
    }
}
