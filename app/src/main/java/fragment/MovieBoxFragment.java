package fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import adapter.MovieAdapter;
import model.MovieTile;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.MainActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import viewModel.MovieBoxViewModel;

public class MovieBoxFragment extends Fragment
{
    private static final String TAG = "MovieBoxFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.moviebox_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView movieRecyclerView = view.findViewById(R.id.moviesRecycler);

        movieRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3, GridLayoutManager.VERTICAL, false));

        final MovieAdapter movieAdapter = new MovieAdapter();
        movieRecyclerView.setAdapter(movieAdapter);

        final MovieBoxViewModel movieBoxViewModel = ViewModelProviders.of(this).get(MovieBoxViewModel.class);

        movieBoxViewModel.getLiveMovieTileFromRepo().observe(this, new Observer<List<MovieTile>>() {
            @Override
            public void onChanged(@Nullable List<MovieTile> movieTiles) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.findViewById(R.id.loadingProgress).setVisibility(View.INVISIBLE);
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
                movieBoxViewModel.sendPlayMovieNsdMessage(
                        ((MainActivity)getActivity()).getNsdHost(),
                        ((MainActivity)getActivity()).getNsdPort(),
                        stringBuilder.toString());

            }
        });
    }
}






















