package api;

import model.OtpResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface OtpInterface {

    @Headers({"Accept: application/json"})
    @GET("{api_key}/SMS/{mobile_number}/AUTOGEN")
    Call<OtpResponse> getOtp(@Path("api_key") String apiKey, @Path("mobile_number") String mobileNumber);

    @Headers({"Accept: application/json"})
    @GET("{api_key}/SMS/VERIFY/{session_id}/{otp_number}")
    Call<OtpResponse> verifyOtp(@Path("api_key") String apiKey, @Path("session_id") String session_id, @Path("otp_number") String otp_number);

}
