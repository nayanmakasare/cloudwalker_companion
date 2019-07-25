package adapter;

import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class NsdAdapter extends ListAdapter<NsdServiceInfo, NsdAdapter.NsdViewHolder>
{
    private static final String TAG = "NsdAdapter";
    private OnItemClickListener listener;

    public NsdAdapter() {
        super(DIFF_CALLBACK);
    }

    private static  final DiffUtil.ItemCallback<NsdServiceInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<NsdServiceInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull NsdServiceInfo nsdServiceInfo, @NonNull NsdServiceInfo t1) {
            if(nsdServiceInfo.getServiceName().equalsIgnoreCase(t1.getServiceName()) &&
            nsdServiceInfo.getServiceType().equalsIgnoreCase(t1.getServiceName())){
                return true;
            }else {
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull NsdServiceInfo nsdServiceInfo, @NonNull NsdServiceInfo t1) {
            if(nsdServiceInfo.getServiceName().equalsIgnoreCase(t1.getServiceName()) &&
                    nsdServiceInfo.getServiceType().equalsIgnoreCase(t1.getServiceName())){
                return true;
            }else {
                return false;
            }
        }
    };


    @Override
    public void submitList(final List<NsdServiceInfo> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    @NonNull
    @Override
    public NsdViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nsd_device_item, viewGroup, false);
        return new NsdViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NsdViewHolder nsdViewHolder, int i) {
        NsdServiceInfo nsdServiceInfo = getItem(i);
        nsdViewHolder.textView.setText(nsdServiceInfo.getServiceName());
    }

    class NsdViewHolder extends RecyclerView.ViewHolder
    {
        private TextView textView;

        public NsdViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.nsdDeviceName);

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
            void onItemClicked(NsdServiceInfo nsdServiceInfo);
    }

    public void setOnItemClickedListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }
}
