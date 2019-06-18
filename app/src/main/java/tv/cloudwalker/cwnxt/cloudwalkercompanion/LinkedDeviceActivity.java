package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapter.LinkedDevicesAdapter;
import appUtils.PreferenceManager;
import room.TvInfo;
import viewModel.LinkedDeviceViewModel;
import viewModel.TvLinkedDeviceViewModel;

public class LinkedDeviceActivity extends AppCompatActivity {

    private static final String TAG = "LinkedDeviceActivity";
    private PreferenceManager preferenceManager;
    private TvLinkedDeviceViewModel tvLinkedDeviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_device);
        getSupportActionBar().setTitle("Linked Devices");
        RecyclerView linkedRecyclerView = findViewById(R.id.linkedDeviceRecyclerView);
        linkedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        preferenceManager = new PreferenceManager(this);
        final LinkedDevicesAdapter linkedDevicesAdapter = new LinkedDevicesAdapter();
        linkedRecyclerView.setAdapter(linkedDevicesAdapter);

        tvLinkedDeviceViewModel = ViewModelProviders.of(this).get(TvLinkedDeviceViewModel.class);
        getLifecycle().addObserver(tvLinkedDeviceViewModel);


//        tvLinkedDeviceViewModel.getAllLinkedDevice().observe(this, new Observer<List<TvInfo>>() {
//            @Override
//            public void onChanged(@Nullable List<TvInfo> tvInfos) {
//                linkedDevicesAdapter.submitList(tvInfos);
//            }
//        });


//        tvLinkedDeviceViewModel.getLinkedDevicesLive().observe(this, new Observer<List<TvInfo>>() {
//            @Override
//            public void onChanged(@Nullable List<TvInfo> tvInfos) {
//                if(tvInfos != null && tvInfos.size() > 0){
//                    linkedDevicesAdapter.submitList(tvInfos);
//                }
//            }
//        });


        tvLinkedDeviceViewModel.getDeleteDeviceStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null && !aBoolean) {

                }
            }
        });


        tvLinkedDeviceViewModel.getLinkedDevicesLive().observe(this, new Observer<List<TvInfo>>() {
            @Override
            public void onChanged(@Nullable List<TvInfo> tvInfos) {
                linkedDevicesAdapter.submitList(tvInfos);
            }
        });


        linkedDevicesAdapter.setOnItemClickedListener(new LinkedDevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(TvInfo tvInfo) {
                tvLinkedDeviceViewModel.deleteLinkDevice(tvInfo);
            }
        });




//        final LinkedDeviceViewModel linkedDeviceViewModel = ViewModelProviders.of(this).get(LinkedDeviceViewModel.class);
//
//        linkedDeviceViewModel.getLiveLinkedDevice().observe(this, new Observer<List<TvInfo>>() {
//            @Override
//            public void onChanged(@Nullable List<TvInfo> tvInfos) {
//                Set<String> linkedDevices = new HashSet<>();
//                for(TvInfo tvInfo : tvInfos){
//                    linkedDevices.add(tvInfo.getEmac());
//                }
//                preferenceManager.setLinkedNsdDevices(linkedDevices);
//                linkedDevicesAdapter.submitList(tvInfos);
//            }
//        });
//
//        linkedDevicesAdapter.setOnItemClickedListener(new LinkedDevicesAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClicked(TvInfo tvInfo) {
//                        linkedDeviceViewModel.removeLinkedDeviceFromRepository(tvInfo);
//            }
//        });
//
//        linkedDeviceViewModel.getRemoveLinkDeviceStatus().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                Log.d(TAG, "onChanged: ");
//                if(!aBoolean) {
//                    getSupportActionBar().setTitle("No Devices are Linked");
//                }
//            }
//        });
    }
}
