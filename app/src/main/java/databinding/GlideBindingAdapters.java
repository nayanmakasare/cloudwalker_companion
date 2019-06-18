package databinding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideBindingAdapters
{
    private static final String TAG = "GlideBindingAdapters";
    
    @BindingAdapter("imageUrl")
    public static void setImageResource(ImageView view, int imageUrl)
    {
        Log.d(TAG, "setImageResource: ");
        Context context = view.getContext();
        view.setImageDrawable(context.getResources().getDrawable(imageUrl));
//        Glide.with(context)
//                .load(imageUrl)
//                .into(view);
    }

    @BindingAdapter("imageUrl")
    public static void setImageResource(ImageView view, String imageUrl)
    {
        Log.d(TAG, "setImageResource: string ");
        Context context = view.getContext();
        Glide.with(context)
                .load(imageUrl)
                .into(view);
    }
}
