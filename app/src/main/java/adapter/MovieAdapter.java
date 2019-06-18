package adapter;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import model.MovieTile;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class MovieAdapter extends ListAdapter<MovieTile, MovieAdapter.MovieViewHolder>
{
    private OnItemClickListener listener;

    public MovieAdapter() {
        super(DIFF_CALLBACK);
    }

    public static  final DiffUtil.ItemCallback<MovieTile> DIFF_CALLBACK = new DiffUtil.ItemCallback<MovieTile>() {
        @Override
        public boolean areItemsTheSame(@NonNull MovieTile movieTile, @NonNull MovieTile t1) {
            if(movieTile.getTid().equalsIgnoreCase(t1.getTid())) {
                return true ;
            }else {
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull MovieTile movieTile, @NonNull MovieTile t1) {
            if(movieTile.getTitle().equalsIgnoreCase(t1.getTitle())) {
                return true ;
            }else {
                return false;
            }
        }
    };

    @Override
    public void submitList(final List<MovieTile> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        MovieTile movieTile = getItem(i);
        movieViewHolder.movieTitle.setText(movieTile.getTitle());
        Glide.with(movieViewHolder.itemView.getContext())
                .load(movieTile.getPortrait())
                .into(movieViewHolder.moviePoster);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
    {
        private TextView movieTitle;
        private ImageView moviePoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            moviePoster = itemView.findViewById(R.id.moviePoster);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null  && position != RecyclerView.NO_POSITION){
                        listener.onItemClicked(getItem(position));
                    }
                }
            });
        }
    }

    public interface  OnItemClickListener{
        void onItemClicked(MovieTile movieTile);
    }

    public void setOnItemClickedListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }
}
