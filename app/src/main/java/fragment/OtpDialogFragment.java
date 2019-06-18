package fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import api.OtpInterface;
import model.OtpResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.CloudwalkerPreferenceActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class OtpDialogFragment extends DialogFragment {

    public static final String TAG = OtpDialogFragment.class.getSimpleName();
    EditText otpEditText;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            window.requestFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(windowParams);
        }
        View v = inflater.inflate(R.layout.otp_fragment, container, false);
        otpEditText = v.findViewById(R.id.OtpEt);
        progressBar = v.findViewById(R.id.OtpProgress);
        Button button = v.findViewById(R.id.OtpButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "verifyOTP: " + getArguments().getString("sessionId"));
                if (otpEditText != null && otpEditText.getText() != null && !otpEditText.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "verifyOTP: in if");
                    new Retrofit.Builder().baseUrl("https://2factor.in/API/V1/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(OtpInterface.class)
                            .verifyOtp("168351ac-7235-11e9-ade6-0200cd936042",
                                    getArguments().getString("sessionId"),
                                    otpEditText.getText().toString())
                            .enqueue(new Callback<OtpResponse>() {
                                @Override
                                public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                                    Log.d(TAG, "onResponse: " + response.code());
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (response.code() == 200 && response.body().getStatus().compareToIgnoreCase("Success") == 0) {
                                        ((CloudwalkerPreferenceActivity) getActivity()).getResultFromOtp(true, getActivity());
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Wrong Otp!!! , Please Enter the correct Otp.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<OtpResponse> call, Throwable t) {
                                    t.printStackTrace();
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Please Enter the Otp.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }


}
