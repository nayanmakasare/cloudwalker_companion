package fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import adapter.NsdAdapter;
import model.NewUserProfile;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.MainActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import viewModel.NsdViewModel;

public class NsdDeviceFragment extends Fragment
{
    private static final String TAG = "NsdDeviceFragment";
    private NsdViewModel nsdViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nsdfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserProfile newUserProfile = getActivity().getIntent().getParcelableExtra(NewUserProfile.class.getSimpleName());

        RecyclerView avalibleRecyclerView =  view.findViewById(R.id.avaliableDeviceRecyclerView);
        avalibleRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final NsdAdapter nsdAdapter = new NsdAdapter();
        avalibleRecyclerView.setAdapter(nsdAdapter);


        nsdViewModel = ViewModelProviders.of(this).get(NsdViewModel.class);

        nsdViewModel.getLiveNsdList().observe(this, new Observer<List<NsdServiceInfo>>() {
            @Override
            public void onChanged(@Nullable List<NsdServiceInfo> nsdServiceInfos) {
                nsdAdapter.submitList(nsdServiceInfos);
            }
        });

        nsdViewModel.getResolveNsdStatus().observe(this, new Observer<NsdServiceInfo>() {
            @Override
            public void onChanged(@Nullable NsdServiceInfo nsdServiceInfo) {
                if(nsdServiceInfo != null && nsdServiceInfo.getHost() != null && nsdServiceInfo.getPort() != 0 ){
                    ((MainActivity)getActivity()).setNsdHost(nsdServiceInfo.getHost().getHostAddress());
                    ((MainActivity)getActivity()).setNsdPort(nsdServiceInfo.getPort());
                    ((MainActivity)getActivity()).setResolvedNsdServiceInfo(nsdServiceInfo);
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
                }
            }
        });

        if(newUserProfile != null && newUserProfile.getLinkedDevices() != null && newUserProfile.getLinkedDevices().size() > 0){
            nsdViewModel.passLinkedDeviceToRepo(newUserProfile.getLinkedDevices());
        }

        nsdAdapter.setOnItemClickedListener(new NsdAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(NsdServiceInfo nsdServiceInfo) {
                nsdViewModel.resolverNsdServiceFromRepository(nsdServiceInfo);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: nsd frag ");
        nsdViewModel.startDiscovery();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: nsd frag ");
        nsdViewModel.stopDiscovery();
    }
}
