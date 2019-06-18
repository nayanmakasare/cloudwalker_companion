package api;

import java.util.List;

import model.MovieResponse;
import model.NewUserProfile;
import room.TvInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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


//    @DELETE("/linkdevice/{googleId}/{tvEmac}")


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

//    @Headers({"Accept: application/json"})
//    @GET("/youtube/v3/search")
//    Call<YoutubePrimeObject> getYoutubeFeeds(@Query("part") String id,
//                                             @Query("maxResults") String maxResults,
//                                             @Query("q") String searchString,
//                                             @Query("key") String developerKey);

}
























