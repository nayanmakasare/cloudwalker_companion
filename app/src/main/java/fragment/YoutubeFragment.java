package fragment;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Activity;
import com.google.api.services.youtube.model.ActivityListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.YoutubeAdapter;
import appUtils.YoutubeSearchLoader;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.MainActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

import static android.app.Activity.RESULT_OK;

public class YoutubeFragment extends Fragment implements EasyPermissions.PermissionCallbacks
{

    public static final String TAG = YoutubeFragment.class.getSimpleName();
    YoutubeAdapter recyclerViewAdapter;
    boolean isLoading = false;
    private RecyclerView recyclerView;
    private static final int SEARCH_VIDEOS_LOADER = 1;
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "Cloudwalker Companion";
    ProgressDialog mProgress;
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Calling YouTube Data API ...");

        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);

        initAdapter();
//        initScrollListener();
        ((SearchView) view.findViewById(R.id.searchView)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchRequestTask(mCredential).execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


//    private void populateData() {
//
//        String search = "GET https://www.googleapis.com/youtube/v3/search?part=snippet&q=avengers&safeSearch=strict HTTP/1.1\n" +
//                "\n" +
//                "Authorization: Bearer [YOUR_ACCESS_TOKEN]\n" +
//                "Accept: application/json\n";
//
//        new Retrofit.Builder()
//                .baseUrl("https://www.googleapis.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build().create(MyProfileInterface.class)
//                .getYoutubeFeeds("snippet","25", "lord krishna", "AIzaSyD09fkr2tGoND9seUYEvs-hc-vnFf18_hQ")
//                .enqueue(new Callback<YoutubePrimeObject>() {
//                    @Override
//                    public void onResponse(Call<YoutubePrimeObject> call, Response<YoutubePrimeObject> response) {
//                        Log.d(TAG, "onResponse: "+response.code()+" "+response.body());
//                        if(response.code() == 200 && response.body().getItems() != null && response.body().getItems().size() > 0) {
//                            recyclerViewAdapter.submitList(response.body().getItems());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<YoutubePrimeObject> call, Throwable t) {
//                        Log.d(TAG, "onFailure: "+t.getMessage());
//                    }
//                });
//    }


//    private void initScrollListener() {
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                if (!isLoading) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size() - 1) {
//                        //bottom of list!
////                        loadMore();
//                        isLoading = true;
//                    }
//                }
//            }
//        });
//    }

    private void initAdapter() {
        recyclerViewAdapter = new YoutubeAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private class SearchRequestTask extends AsyncTask<String, Void, List<SearchResult>>
    {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        public SearchRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Cloudwalker Companion")
                    .build();
        }

        @Override
        protected List<SearchResult> doInBackground(String... strings) {
            try {
                return getDataFromApi(strings[0]);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }


        private List<SearchResult> getDataFromApi(String searchQuery) throws IOException {
            SearchListResponse searchListResponse =    mService.search()
                    .list("snippet")
                    .setQ(searchQuery)
                    .setMaxResults((long)10)
                    .execute();
            return searchListResponse.getItems();
        }

        @Override
        protected void onPostExecute(List<SearchResult> searchResults) {
            super.onPostExecute(searchResults);
            recyclerViewAdapter.submitList(searchResults);
        }
    }


    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Cloudwalker Companion")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<String>();
//            mService.search().list("snippet").setQ()




            SearchListResponse searchListResponse = mService.search()
                    .list("snippet")
                    .setQ("god")
                    .setMaxResults((long)50)
                    .execute();

            for(SearchResult searchResult : searchListResponse.getItems()){
                Log.d(TAG, "getDataFromApi: "+searchResult.getSnippet().getTitle());
            }


            ActivityListResponse activityListResponse = mService.activities().list("snippet,contentDetails")
                    .setMaxResults((long)50)
                    .setHome(true)
                    .execute();

            for(Activity activity : activityListResponse.getItems())
            {
                Log.d(TAG, "getDataFromApi: title "+ activity.getSnippet().getTitle());
                Log.d(TAG, "getDataFromApi: id "+activity.getId());
            }
            return channelInfo;
        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                Toast.makeText(getActivity(), "No results returned.", Toast.LENGTH_LONG).show();
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
                Toast.makeText(getActivity(), TextUtils.join("\n", output), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            YoutubeFragment.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getActivity(),"The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_LONG ).show();

                    Log.d(TAG, "onCancelled: "+mLastError.getLocalizedMessage());
                }
            } else {
                Toast.makeText(getActivity(), "Request cancelled.", Toast.LENGTH_LONG).show();
            }
        }
    }




    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_LONG ).show();
        } else {
//            new MakeRequestTask(mCredential).execute();
            new SearchRequestTask(mCredential).execute("god");
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getActivity(),"This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG ).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                (MainActivity)getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
