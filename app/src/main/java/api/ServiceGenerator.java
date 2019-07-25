package api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

//    public static final String BASE_URL = "http://dev-tvapi.cloudwalker.tv/";
    public static final String BASE_URL = "http://192.168.1.143:5081/";

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static MyProfileInterface apiInterface = retrofit.create(MyProfileInterface.class);

    public static MyProfileInterface getRequestApi() { return  apiInterface; }
}
