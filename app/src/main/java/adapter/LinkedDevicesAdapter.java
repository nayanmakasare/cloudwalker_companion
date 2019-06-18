package adapter;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import room.TvInfo;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;

public class LinkedDevicesAdapter extends ListAdapter<TvInfo, LinkedDevicesAdapter.LinkedDeviceViewHolder>
{

    private OnItemClickListener listener;

    public LinkedDevicesAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<TvInfo> DIFF_CALLBACK  = new DiffUtil.ItemCallback<TvInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull TvInfo tvInfo, @NonNull TvInfo t1) {
            if(tvInfo.getEmac().equalsIgnoreCase(t1.getEmac()) && tvInfo.getBoardName().equalsIgnoreCase(t1.getBoardName())){
                return true ;
            }else {
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull TvInfo tvInfo, @NonNull TvInfo t1) {
            if(tvInfo.getEmac().equalsIgnoreCase(t1.getEmac()) && tvInfo.getBoardName().equalsIgnoreCase(t1.getBoardName())){
                return true ;
            }else {
                return false;
            }
        }
    };

    @Override
    public void submitList(final List<TvInfo> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }


    @NonNull
    @Override
    public LinkedDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linked_device_item, viewGroup, false);
        return new LinkedDeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkedDeviceViewHolder linkedDeviceViewHolder, int i) {
        TvInfo tvInfo = getItem(i);
        linkedDeviceViewHolder.textView.setText("CloudTv_"+tvInfo.getEmac());
    }

    class LinkedDeviceViewHolder extends RecyclerView.ViewHolder
    {
        private TextView textView;
        private Button removeLinkedDevice;

        public LinkedDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.nsdDeviceName);
            removeLinkedDevice = itemView.findViewById(R.id.remove_linked_device);

            removeLinkedDevice.setOnClickListener(new View.OnClickListener() {
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
        void onItemClicked(TvInfo tvInfo);
    }

    public void setOnItemClickedListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }
}
