package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;

import appUtils.PreferenceManager;
import de.hdodenhof.circleimageview.CircleImageView;
import model.NewUserProfile;

public class IntermideateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermideate);
        PreferenceManager preferenceManager = new PreferenceManager(IntermideateActivity.this);
        if(preferenceManager.isGoogleSignIn() && preferenceManager.isCloudwalkerSigIn()){
            startActivity(new Intent(IntermideateActivity.this, MainActivity.class));
            onBackPressed();
        }else if(preferenceManager.isGoogleSignIn() && !preferenceManager.isCloudwalkerSigIn()) {
            NewUserProfile newUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
            Intent prefrenceIntent = new Intent(IntermideateActivity.this, CloudwalkerPreferenceActivity.class);
            prefrenceIntent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
            startActivity(prefrenceIntent);
            onBackPressed();
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
}
