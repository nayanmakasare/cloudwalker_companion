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

import java.util.ArrayList;
import java.util.List;

import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class CwLinkedDevicesAdapter extends ListAdapter<String, CwLinkedDevicesAdapter.CwLinkedDeviceViewHolder>
{

    private CwLinkedDevicesAdapter.OnItemClickListener listener;

    public CwLinkedDevicesAdapter() {
        super(DIFF_CALLBACK);
    }

    public static  final DiffUtil.ItemCallback<String> DIFF_CALLBACK = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String movieTile, @NonNull String t1) {
            return movieTile.equals(t1);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String movieTile, @NonNull String t1) {
            return movieTile.equals(t1);
        }
    };


    @Override
    public void submitList(final List<String> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }


    @NonNull
    @Override
    public CwLinkedDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cw_linked_device_item, viewGroup, false);
        return new CwLinkedDeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CwLinkedDeviceViewHolder cwNsdViewHolder, int i) {
        String nsdServiceInfo = getItem(i);
        cwNsdViewHolder.textView.setText(nsdServiceInfo);
    }

    class CwLinkedDeviceViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;

        public CwLinkedDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.networkDevice);
            imageView = itemView.findViewById(R.id.deleteLinkedDevice);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null  && position != RecyclerView.NO_POSITION){
                        listener.onDeviceClicked(getItem(position));
                    }
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null  && position != RecyclerView.NO_POSITION){
                        listener.onDeleteLinkedClicked(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onDeviceClicked(String nsdServiceInfo);
        void onDeleteLinkedClicked(String nsdServiceInfo);
    }

    public void setOnItemClickedListener(CwLinkedDevicesAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

}
