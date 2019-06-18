package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import model.NewUserProfile;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityCloudwalkerPreferenceBinding;
import utils.AppUtils;
import viewModel.CloudwalkerPreferenceViewModel;

public class CloudwalkerPreferenceActivity extends AppCompatActivity {

    public static final String TAG = CloudwalkerPreferenceActivity.class.getSimpleName();
    private CloudwalkerPreferenceViewModel cloudwalkerPreferenceViewModel;
    private ActivityCloudwalkerPreferenceBinding activityCloudwalkerPreferenceBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //databinding
        activityCloudwalkerPreferenceBinding = DataBindingUtil.setContentView(this, R.layout.activity_cloudwalker_preference);
        activityCloudwalkerPreferenceBinding.setActivity(CloudwalkerPreferenceActivity.this);
        getSupportActionBar().hide();
        try {
            JSONObject profileConfigObj = new JSONObject(AppUtils.loadJSONFromAsset(this));
            activityCloudwalkerPreferenceBinding.mySpinner1.setItems(getArrayListFromJSON(profileConfigObj.getJSONArray("genres")));
            activityCloudwalkerPreferenceBinding.mySpinner2.setItems(getArrayListFromJSON(profileConfigObj.getJSONArray("languages")));
            activityCloudwalkerPreferenceBinding.mySpinner3.setItems(getArrayListFromJSON(profileConfigObj.getJSONArray("content_type")));
            hideSoftKeyboard();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        activityCloudwalkerPreferenceBinding.userDob.setOnClickListener(new View.OnClickListener() {
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
                        activityCloudwalkerPreferenceBinding.userDob.setText(date);
                    }
                };

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(CloudwalkerPreferenceActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        //viewModel
        cloudwalkerPreferenceViewModel = ViewModelProviders.of(this).get(CloudwalkerPreferenceViewModel.class);
        cloudwalkerPreferenceViewModel.getCreateProfileStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    if (aBoolean) {
                        startActivity(new Intent(CloudwalkerPreferenceActivity.this, MainActivity.class));
                        onBackPressed();
                    } else {
                        Toast.makeText(CloudwalkerPreferenceActivity.this, "Please Check your Internet.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cloudwalkerPreferenceViewModel.checkIfGoogleAndCloudwalkerLogin();

        cloudwalkerPreferenceViewModel.getBothLoginStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    if (aBoolean) {
                        startActivity(new Intent(CloudwalkerPreferenceActivity.this, MainActivity.class));
                        onBackPressed();
                    }
                }
            }
        });
    }

    private void hideSoftKeyboard() {
        activityCloudwalkerPreferenceBinding.mySpinner1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow((findViewById(R.id.userMobile)).getWindowToken(), 0);
                return false;
            }
        });
        activityCloudwalkerPreferenceBinding.mySpinner2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow((findViewById(R.id.userMobile)).getWindowToken(), 0);
                return false;
            }
        });
        activityCloudwalkerPreferenceBinding.mySpinner3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow((findViewById(R.id.userMobile)).getWindowToken(), 0);
                return false;
            }
        });
    }


    public void onCreateProfileClicked(View view) {

        Log.d(TAG, "onCreateProfileClicked: genre " + activityCloudwalkerPreferenceBinding.mySpinner1.getSelectedStrings().size());
        Log.d(TAG, "onCreateProfileClicked: lang " + activityCloudwalkerPreferenceBinding.mySpinner2.getSelectedStrings().size());
        Log.d(TAG, "onCreateProfileClicked: content " + activityCloudwalkerPreferenceBinding.mySpinner3.getSelectedStrings().size());

        if (activityCloudwalkerPreferenceBinding.mySpinner1.getSelectedStrings().size() > 0 &&
                activityCloudwalkerPreferenceBinding.mySpinner2.getSelectedStrings().size() > 0 &&
                activityCloudwalkerPreferenceBinding.mySpinner3.getSelectedStrings().size() > 0 &&
                activityCloudwalkerPreferenceBinding.userMobile.getText().length() > 0 &&
                activityCloudwalkerPreferenceBinding.userMobile.getText().length() == 10) {
            Log.d(TAG, "goToMain: ");
            getResultFromOtp(true, view.getContext());

//            new Retrofit.Builder().baseUrl("https://2factor.in/API/V1/")
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//                    .create(OtpInterface.class)
//                    .getOtp("168351ac-7235-11e9-ade6-0200cd936042", ((EditText) findViewById(R.id.userMobile)).getText().toString())
//                    .enqueue(new Callback<OtpResponse>() {
//                        @Override
//                        public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
//                            Log.d(TAG, "onResponse: " + response.code());
//                            if (response.code() == 200 && response.body().getStatus().equals("Success")) {
//                                OtpDialogFragment dialogFragment = new OtpDialogFragment();
//                                Bundle bundle = new Bundle();
//                                bundle.putString("sessionId", response.body().getDetails());
//                                dialogFragment.setArguments(bundle);
//                                dialogFragment.show(getFragmentManager(), "Configure");
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<OtpResponse> call, Throwable t) {
//                            Log.d(TAG, "onFailure: " + t.getMessage());
//                        }
//                    });
        } else {
            Toast.makeText(view.getContext(), "Please Check all the fields are filled and mobile number is of 10 digits.", Toast.LENGTH_SHORT).show();
        }
    }

    private NewUserProfile makeNewUserProfileObject() {
        NewUserProfile newUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
        newUserProfile.setGenre(activityCloudwalkerPreferenceBinding.mySpinner1.getSelectedStrings());
        Log.d(TAG, "makeNewUserProfileObject: email " + newUserProfile.getEmail());
        Log.d(TAG, "makeNewUserProfileObject: username " + newUserProfile.getUserName());
        newUserProfile.setLaunguage(activityCloudwalkerPreferenceBinding.mySpinner2.getSelectedStrings());
        newUserProfile.setContentType(activityCloudwalkerPreferenceBinding.mySpinner3.getSelectedStrings());
        newUserProfile.setMobileNumber(activityCloudwalkerPreferenceBinding.userMobile.getText().toString());
        newUserProfile.setDob(activityCloudwalkerPreferenceBinding.userDob.getText().toString());
        if (activityCloudwalkerPreferenceBinding.radioSex.getCheckedRadioButtonId() == R.id.radioMale) {
            newUserProfile.setGender("male");
        } else {
            newUserProfile.setGender("female");
        }
        return newUserProfile;
    }

    private ArrayList<String> getArrayListFromJSON(JSONArray jsonArray) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList.add(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }


    public void getResultFromOtp(Boolean isOtpVerified, Context context) {
        Log.d(TAG, "getResultFromOtp: ");
        cloudwalkerPreferenceViewModel.createNewProfile(makeNewUserProfileObject(), isOtpVerified, context);
    }
}

