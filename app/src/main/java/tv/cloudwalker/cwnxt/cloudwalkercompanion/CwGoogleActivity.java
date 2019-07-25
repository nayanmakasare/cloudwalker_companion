package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.SignInButton;

import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityCwGoogleBinding;

public class CwGoogleActivity extends AppCompatActivity {

    private ActivityCwGoogleBinding activityCwGoogleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCwGoogleBinding = DataBindingUtil.setContentView(this, R.layout.activity_cw_google);
        getSupportActionBar().hide();
        activityCwGoogleBinding.googleButton.setSize(SignInButton.SIZE_WIDE);
        activityCwGoogleBinding.googleButton.setColorScheme(SignInButton.COLOR_DARK);
    }
}
