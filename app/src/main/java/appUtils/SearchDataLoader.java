package appUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import api.ApiClient;
import api.CustomHttpClient;
import api.MyProfileInterface;
import model.MovieRow;
import model.MovieTile;
import model.Search;
import model.SearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchDataLoader extends Loader<List<MovieTile>> {

    private MovieRow movieRow;
    private MovieTile mMoreMovieTile;
    private List<MovieTile> movieTile = new ArrayList<>();
    private List<MovieTile> searchRows = new ArrayList<>();
    private String pathID;
    private Bundle mBundle;
    private String mQuery;
//    private static final String appId = "58f07270685892493fe98bc8";
    private static final String appId = "59a3db1dcc04dac1fe27a4f0"; // ("Movies,Documentary,Music,Short Film")
    public static final String TAG = SearchDataLoader.class.getSimpleName();


    public SearchDataLoader(Context context, Bundle bundle) {
        super(context);
        mBundle = bundle;
    }


    @Override
    protected void onStartLoading() {
        if (searchRows != null) {
            //deliverResult(moreDataList);
            forceLoad();
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        mQuery = mBundle.getString("query");
        try {

//            ApiClient.getClient(getContext().getApplicationContext()).create(MyProfileInterface.class);

            MyProfileInterface apiService = new Retrofit.Builder()
                    .baseUrl("http://tvapi.cloudwalker.tv/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(CustomHttpClient.getHttpClient(getContext(),"http://tvapi.cloudwalker.tv/"))
                    .build()
                    .create(MyProfileInterface.class);

            Search search = new Search(appId, mQuery);
            Call<SearchResponse> call = apiService.getSearchResponse(search);
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    Log.d(TAG, "onResponse: "+response.code());
                    if (response.code() == 200 && response.body() != null && response.body().getRows().getRowItems().size() > 0) {
                        searchRows = response.body().getRows().getRowItems();
                        deliverResult(searchRows);
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d(TAG, "onFailure: "+t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopLoading() {
        super.stopLoading();
    }


    @Override
    protected void onReset() {
        //cancel api call.
        super.onReset();
    }

    @Override
    public void deliverResult(List<MovieTile> data) {
//        Timber.d("SearchDataLoader deliverResult  " + data.size());
        //movieTile = data;
        if (isStarted()) {
            super.deliverResult(data);
        }

    }
}
