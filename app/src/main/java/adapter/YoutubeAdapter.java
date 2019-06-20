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
import com.google.api.services.youtube.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class YoutubeAdapter  extends ListAdapter<SearchResult, YoutubeAdapter.YoutubeViewHolder>
{

    public YoutubeAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<SearchResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<SearchResult>() {
        @Override
        public boolean areItemsTheSame(@NonNull SearchResult searchResult, @NonNull SearchResult t1) {
            return searchResult.getSnippet().getTitle().equalsIgnoreCase(t1.getSnippet().getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchResult searchResult, @NonNull SearchResult t1) {
            return searchResult.getSnippet().getTitle().equalsIgnoreCase(t1.getSnippet().getTitle());
        }
    };

    
    

    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.youtube_item, viewGroup, false);
        return new YoutubeViewHolder(view);
    }

    @Override
    public void submitList(final List<SearchResult> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }


    @Override
    public void onBindViewHolder(@NonNull YoutubeViewHolder youtubeViewHolder, int i) {
        SearchResult searchResult = getItem(i);
        youtubeViewHolder.textView.setText(searchResult.getSnippet().getTitle());
        Glide.with(youtubeViewHolder.itemView.getContext())
                .load(searchResult.getSnippet().getThumbnails().getMedium().getUrl())
                .into(youtubeViewHolder.imageView);
    }

    class YoutubeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        private YoutubeViewHolder(@NonNull View SearchResultView) {
            super(SearchResultView);
            imageView = SearchResultView.findViewById(R.id.youtubePoster);
            textView = SearchResultView.findViewById(R.id.youtubeTitle);
        }
    }

}






















