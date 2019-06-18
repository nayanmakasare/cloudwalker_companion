package appUtils;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import model.ProfileInfoList;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ProfileListItemBinding;

public class CustomAdapter extends BaseAdapter {

    private List<ProfileInfoList> listProfileInfoList;
    private Activity activity;

    public CustomAdapter(Activity activity, List<ProfileInfoList> listProfileInfoList){
        this.listProfileInfoList = listProfileInfoList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return listProfileInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return listProfileInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //to be implemented
        ProfileListItemBinding binding;
        if(convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.profile_list_item, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }else{
            binding = (ProfileListItemBinding) convertView.getTag();
        }
        binding.setProfileInfo(listProfileInfoList.get(position));
        return binding.getRoot();
    }
}