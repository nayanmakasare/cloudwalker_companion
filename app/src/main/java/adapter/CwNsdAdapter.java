package adapter;

import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class CwNsdAdapter extends ListAdapter<NsdServiceInfo, CwNsdAdapter.CwNsdViewHolder>
{
    private OnItemClickListener listener;

    public CwNsdAdapter() {
        super(DIFF_CALLBACK);
    }

    public static  final DiffUtil.ItemCallback<NsdServiceInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<NsdServiceInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull NsdServiceInfo movieTile, @NonNull NsdServiceInfo t1) {
            return movieTile.getServiceName().equalsIgnoreCase(t1.getServiceName()) &&
                    movieTile.getServiceType().equals(t1.getServiceType()) &&
                    movieTile.getPort() == t1.getPort();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NsdServiceInfo movieTile, @NonNull NsdServiceInfo t1) {
            return movieTile.getServiceName().equalsIgnoreCase(t1.getServiceName()) &&
                    movieTile.getServiceType().equals(t1.getServiceType()) &&
                    movieTile.getPort() == t1.getPort();
        }
    };


    @Override
    public void submitList(final List<NsdServiceInfo> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }


    @NonNull
    @Override
    public CwNsdViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cw_network_device, viewGroup, false);
        return new CwNsdViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CwNsdViewHolder cwNsdViewHolder, int i) {
        NsdServiceInfo nsdServiceInfo = getItem(i);
        cwNsdViewHolder.textView.setText(nsdServiceInfo.getServiceName());
    }

    class CwNsdViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public CwNsdViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.networkDevice);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null  && position != RecyclerView.NO_POSITION){
                        listener.onDeviceClicked(getItem(position));
                    }
                }
            });
        }
    }

    public interface  OnItemClickListener{
        void onDeviceClicked(NsdServiceInfo nsdServiceInfo);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
