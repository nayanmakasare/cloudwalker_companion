package api;

import java.util.List;

import model.MovieResponse;
import model.NewUserProfile;
import model.OAuthToken;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import room.TvInfo;

public interface MyProfileInterface {

    @Headers({"Accept: application/json"})
    @POST("/profileuser")
    Call<ResponseBody> postUserProfile(@Body NewUserProfile userProfileObj);

    @GET("/profile/{googleId}")
    Call<NewUserProfile> getUserProfile(@Path("googleId") String googleId);


    @PUT("/profile/{googleId}")
    Call<NewUserProfile> modifyUserProfile(@Body NewUserProfile newUserProfile, @Path("googleId") String googleId);

    @Headers({"Accept: application/json"})
    @PUT("/linkdevice/{googleId}")
    Call<TvInfo> postNewTvDevice(@Body TvInfo tvInfoListObj, @Path("googleId") String googleId);


    @GET("/linkdevice/{googleId}")
    Call<List<TvInfo>> getLinkDevices(@Path("googleId") String googleId);


    @Headers({"Accept: application/json"})
    @HTTP(method = "DELETE", path = "/linkdevice/{googleId}/{tvEmac}", hasBody = true)
    Call<ResponseBody> removeTvDevice(@Body TvInfo tvInfoListObj,
                                      @Path("googleId") String googleId,
                                      @Path("tvEmac") String tvEmac);

    @DELETE("/profile/{googleId}")
    Call<ResponseBody> deleteProfile(@Path("googleId") String googleId);

    @Headers({"Accept-Version: 1.0.0"})
    @GET("cats.json")
    Call<MovieResponse> getHomeScreenData();


    @FormUrlEncoded
    @POST("oauth2/v4/token")
    Call<OAuthToken> requestTokenForm(
            @Field("code")String code,
            @Field("client_id")String client_id,
//            @Field("client_secret")String client_secret, //Is not relevant for Android application
            @Field("redirect_uri")String redirect_uri,
            @Field("grant_type")String grant_type);

    /**
     * The call to refresh a token
     */
    @FormUrlEncoded
    @POST("oauth2/v4/token")
    Call<OAuthToken> refreshTokenForm(
            @Field("refresh_token")String refresh_token,
            @Field("client_id")String client_id,
//            @Field("client_secret")String client_secret, //Is not relevant for Android application
            @Field("grant_type")String grant_type);

}
























