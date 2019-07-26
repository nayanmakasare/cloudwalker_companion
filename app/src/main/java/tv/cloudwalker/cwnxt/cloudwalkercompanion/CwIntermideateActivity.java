package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import appUtils.PreferenceManager;
import model.NewUserProfile;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityCwIntermidiateBinding;

public class CwIntermideateActivity extends AppCompatActivity  {
    private static final String TAG = "CwIntermideateActivity";
    private NewUserProfile newUserProfile;
    private ActivityCwIntermidiateBinding activityCwIntermidiateBinding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCwIntermidiateBinding = DataBindingUtil.setContentView(this, R.layout.activity_cw_intermidiate);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        Log.d(TAG, "onCreate: google status "+preferenceManager.getGoogleSignInStatus());
        Log.d(TAG, "onCreate: interm status "+preferenceManager.getCwIntermidiateStatus());
        Log.d(TAG, "onCreate: Pref status "+preferenceManager.getCwPrefrenceStatus());
        Log.d(TAG, "onCreate: Google id "+preferenceManager.getGoogleId());

        getSupportActionBar().hide();
        newUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
        Log.d(TAG, "onCreate: "+newUserProfile);
        if(newUserProfile == null){
            clearAndGoToGoogleSignIn(this);
        }
        if(newUserProfile.getEmail() != null){
            activityCwIntermidiateBinding.emailET.setText(newUserProfile.getEmail());
        }

        Glide.with(this)
                .load(newUserProfile.getImageUrl())
                .into(activityCwIntermidiateBinding.profileImageView);

        activityCwIntermidiateBinding.dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        if (year > 2013) {
                            Toast.makeText(datePicker.getContext(), "Age criteria does not match", Toast.LENGTH_LONG).show();
                            return;
                        }
                        activityCwIntermidiateBinding.dobET.setText(date);
                    }
                };

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(CwIntermideateActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        activityCwIntermidiateBinding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activityCwIntermidiateBinding.dobET.getText().length() >0 &&
                        activityCwIntermidiateBinding.mobileET.getText().length() > 0 &&
                        activityCwIntermidiateBinding.mobileET.getText().length() <=10 &&
                        activityCwIntermidiateBinding.userNameET.getText().length() > 0)
                {
                    PreferenceManager preferenceManager = new PreferenceManager(v.getContext());
                    preferenceManager.setCwIntermidiateStatus(true);
                    preferenceManager.setGoogleSigninStatus(true);
                    newUserProfile.setUserName(activityCwIntermidiateBinding.userNameET.getText().toString());
                    newUserProfile.setMobileNumber(activityCwIntermidiateBinding.mobileET.getText().toString());
                    newUserProfile.setDob(activityCwIntermidiateBinding.dobET.getText().toString());

                    Intent intent = new Intent(v.getContext(), CwPreferenceActivity.class);
                    intent.putExtra(NewUserProfile.class.getSimpleName(), newUserProfile);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(CwIntermideateActivity.this, "Please fill all the Text fields correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearAndGoToGoogleSignIn(Context context){
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setGoogleSigninStatus(false);
        preferenceManager.setCwIntermidiateStatus(false);
        preferenceManager.setCwPrefrenceStatus(false);
        startActivity(new Intent(this, CwGoogleActivity.class));
        finish();
    }
}
