package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import model.NewUserProfile;
import model.TvInfo;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityEditProfileBinding;
import utils.AppUtils;
import viewModel.EditProfileViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ActivityEditProfileBinding activityEditProfileBinding;
    private EditProfileViewModel editProfileViewModel;
    private Boolean isMobileNumberChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //databinding
        activityEditProfileBinding = DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);
        activityEditProfileBinding.setActivity(this);
        getSupportActionBar().hide();
        NewUserProfile currentUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
        Log.d(TAG, "onCreate: obj "+currentUserProfile);
        activityEditProfileBinding.setCurrentUserProfile(currentUserProfile);
        try {
            JSONObject profileConfigObj = new JSONObject(AppUtils.loadJSONFromAsset(this));
            activityEditProfileBinding.genreSpinner.setItems(getArrayListFromJSON(profileConfigObj.getJSONArray("genres")));
            activityEditProfileBinding.languageSpinner.setItems(getArrayListFromJSON(profileConfigObj.getJSONArray("languages")));
            activityEditProfileBinding.contentTypeSpinner.setItems(getArrayListFromJSON(profileConfigObj.getJSONArray("content_type")));
            hideSoftKeyboard();
            activityEditProfileBinding.genreSpinner.setSelection(currentUserProfile.getGenre());
            activityEditProfileBinding.languageSpinner.setSelection(currentUserProfile.getLaunguage());
            activityEditProfileBinding.contentTypeSpinner.setSelection(currentUserProfile.getContentType());
            Log.d(TAG, "onCreate: gender "+currentUserProfile.getGender());
            if(currentUserProfile.getGender().equalsIgnoreCase("male")){
                activityEditProfileBinding.editRadioButton.check(R.id.radioMale);
            }else {
                activityEditProfileBinding.editRadioButton.check(R.id.radioFemale);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        activityEditProfileBinding.mobileET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isMobileNumberChange = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        activityEditProfileBinding.dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        if(year > 2013){
                            Toast.makeText(datePicker.getContext(), "Age criteria does not match", Toast.LENGTH_LONG).show();
                            return;
                        }
                        activityEditProfileBinding.dobEditText.setText(date);
                    }
                };

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EditProfileActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        //viewModel
        editProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);


        editProfileViewModel.getEditProfileStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null && aBoolean) {
                    onBackPressed();
                }
            }
        });
    }


    private void hideSoftKeyboard() {
        activityEditProfileBinding.genreSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.mobileET)).getWindowToken(), 0);
                return false;
            }
        });
        activityEditProfileBinding.contentTypeSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.mobileET)).getWindowToken(), 0);
                return false;
            }
        });
        activityEditProfileBinding.languageSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.mobileET)).getWindowToken(), 0);
                return false;
            }
        });
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


    private NewUserProfile makeNewUserProfileObject() {
        NewUserProfile newUserProfile = getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());
        newUserProfile.setGenre(activityEditProfileBinding.genreSpinner.getSelectedStrings());
        Log.d(TAG, "makeNewUserProfileObject: email "+newUserProfile.getEmail());
        Log.d(TAG, "makeNewUserProfileObject: username "+newUserProfile.getUserName());
        Log.d(TAG, "makeNewUserProfileObject: language "+activityEditProfileBinding.languageSpinner.getSelectedStrings());
        newUserProfile.setLaunguage(activityEditProfileBinding.languageSpinner.getSelectedStrings());
        newUserProfile.setContentType(activityEditProfileBinding.contentTypeSpinner.getSelectedStrings());
        newUserProfile.setMobileNumber(activityEditProfileBinding.mobileET.getText().toString());
        newUserProfile.setDob(activityEditProfileBinding.dobEditText.getText().toString());
        if(activityEditProfileBinding.editRadioButton.getCheckedRadioButtonId() == R.id.radioMale) {
            newUserProfile.setGender("male");
        }else {
            newUserProfile.setGender("female");
        }
        Log.d(TAG, "makeNewUserProfileObject: linked Device "+newUserProfile.getLinkedDevices().size());
        for(TvInfo tvInfo : newUserProfile.getLinkedDevices()){
            Log.d(TAG, "makeNewUserProfileObject: emac  "+tvInfo.getEmac());
            Log.d(TAG, "makeNewUserProfileObject: boardName "+tvInfo.getBoardName());
            Log.d(TAG, "makeNewUserProfileObject: panel "+tvInfo.getPanelName());
        }
        return newUserProfile;
    }

    public void onEditProfileClicked(View view)
    {
        Log.d(TAG, "onCreateProfileClicked: genre "+activityEditProfileBinding.genreSpinner.getSelectedStrings().size());
        Log.d(TAG, "onCreateProfileClicked: lang "+activityEditProfileBinding.languageSpinner.getSelectedStrings().size());
        Log.d(TAG, "onCreateProfileClicked: content "+activityEditProfileBinding.contentTypeSpinner.getSelectedStrings().size());

        if(activityEditProfileBinding.genreSpinner.getSelectedStrings().size() > 0 &&
                activityEditProfileBinding.languageSpinner.getSelectedStrings().size() > 0 &&
                activityEditProfileBinding.contentTypeSpinner.getSelectedStrings().size() > 0 &&
                activityEditProfileBinding.mobileET.getText().length() > 0 &&
                activityEditProfileBinding.mobileET.getText().length() == 10)
        {
            Log.d(TAG, "goToMain: ");
            if(isMobileNumberChange)
            {
                Log.d(TAG, "onEditProfileClicked: mobile number changed ");
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
            }
            else {
                getResultFromOtp(true, view.getContext());
            }
        }
        else {
            Toast.makeText(view.getContext(), "Please Check all the fields are filled and mobile number is of 10 digits.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getResultFromOtp(Boolean isOtpVerified, Context context) {
        Log.d(TAG, "getResultFromOtp: ");
        editProfileViewModel.editProfileApiCall(makeNewUserProfileObject(), isOtpVerified, context);
    }

}
