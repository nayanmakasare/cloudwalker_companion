package appUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YoutubeSearchLoader extends Loader<List<SearchResult>>
{
    private static final String TAG = "YoutubeSearchLoader";
    private List<SearchResult> searchRows = new ArrayList<>();
    private com.google.api.services.youtube.YouTube mService = null;
    private String mQuery;
    private Bundle mBundle;

    public YoutubeSearchLoader(@NonNull Context context, GoogleAccountCredential credential, Bundle bundle) {
        super(context);
        mBundle = bundle;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Cloudwalker Companion")
                .build();
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: ");
        if (searchRows != null) {
            forceLoad();
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        Log.d(TAG, "onForceLoad: ");
        mQuery = mBundle.getString("query");
        if(mQuery != null && !TextUtils.isEmpty(mQuery))
        {
            Log.d(TAG, "onForceLoad: "+mQuery);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        searchRows = mService.search()
                                .list("snippet")
                                .setQ(mQuery)
                                .execute()
                                .getItems();
                        deliverResult(searchRows);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void stopLoading() {
        Log.d(TAG, "stopLoading: ");
        super.stopLoading();
    }


    @Override
    protected void onReset() {
        Log.d(TAG, "onReset: ");
        super.onReset();
    }

    @Override
    public void deliverResult(List<SearchResult> data) {
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
