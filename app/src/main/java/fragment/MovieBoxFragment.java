package fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import adapter.MovieAdapter;
import appUtils.PreferenceManager;
import appUtils.SearchDataLoader;
import model.MovieTile;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.CwNsdListActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import viewModel.MovieBoxViewModel;

public class MovieBoxFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MovieTile>>
{
    private static final String TAG = "MovieBoxFragment";
    private static final int SEARCH_VIDEOS_LOADER = 1;
    private MovieAdapter movieAdapter = new MovieAdapter();
    private ProgressBar progressBar;
    private SearchView searchView ;
    private PreferenceManager preferenceManager;
    private Set<String> linkedDevices;
//    private NsdManager nsdManager;
    private InputMethodManager imm = ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.moviebox_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView movieRecyclerView = view.findViewById(R.id.moviesRecycler);
//        nsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);
        movieRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3, GridLayoutManager.VERTICAL, false));
        movieRecyclerView.setAdapter(movieAdapter);

        progressBar =  view.findViewById(R.id.loadingProgress);
        searchView =  view.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                getLoaderManager().restartLoader(SEARCH_VIDEOS_LOADER, bundle, MovieBoxFragment.this);
                progressBar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final MovieBoxViewModel movieBoxViewModel = ViewModelProviders.of(this).get(MovieBoxViewModel.class);

        movieBoxViewModel.getLiveMovieTileFromRepo().observe(this, new Observer<List<MovieTile>>() {
            @Override
            public void onChanged(@Nullable List<MovieTile> movieTiles) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                movieAdapter.submitList(movieTiles);
            }
        });

        movieAdapter.setOnItemClickedListener(new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(MovieTile movieTile) {
                Log.d(TAG, "onItemClicked: ");
                StringBuilder stringBuilder = new StringBuilder("cwplay~");
                stringBuilder.append(movieTile.getPackageName());
                stringBuilder.append("~");
                stringBuilder.append(movieTile.getTarget().get(0));
                stringBuilder.append("~");
                stringBuilder.append(movieTile.getType());
                communication(stringBuilder.toString());
            }
        });
    }

    private void communication(final String message){
        if(preferenceManager.getNsdPort() > 0 ){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket communicationSocket = new Socket();
                        communicationSocket.connect(new InetSocketAddress(preferenceManager.getNsdHostAddress(), preferenceManager.getNsdPort()));
                        DataOutputStream os = new DataOutputStream(communicationSocket.getOutputStream());
                        os.write(message.getBytes());
                        os.flush();
                        os.close();
                        communicationSocket.close();
                        Thread.currentThread().interrupt();
                    } catch (IOException e) {
                        e.printStackTrace();
                        startActivityForResult(new Intent(getActivity(), CwNsdListActivity.class), 700);
                    }
                }
            }).start();
        }else {
            Toast.makeText(getActivity(), "Not able to connect to the device, please try to connect again.", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(getActivity(), CwNsdListActivity.class), 700);
        }
    }

    @NonNull
    @Override
    public Loader<List<MovieTile>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == SEARCH_VIDEOS_LOADER) {
            return new SearchDataLoader(getActivity(), bundle);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieTile>> loader, List<MovieTile> movieTileList) {
        if(imm == null){
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        if (loader.getId() == SEARCH_VIDEOS_LOADER && movieTileList.size() > 0)
        {
            movieAdapter.submitList(movieTileList);
            progressBar.setVisibility(View.INVISIBLE);
        }
        getLoaderManager().destroyLoader(SEARCH_VIDEOS_LOADER);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieTile>> loader) {

    }
}






















