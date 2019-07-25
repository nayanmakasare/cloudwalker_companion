package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;

import java.util.List;

import repository.NsdFinalRepository;
import model.TvInfo;

public class NsdViewModel extends AndroidViewModel implements LifecycleObserver
{
    private static final String TAG = "NsdViewModel";
    private NsdFinalRepository nsdFinalRepository;

    public NsdViewModel(@NonNull Application application) {
        super(application);
        nsdFinalRepository = new NsdFinalRepository(application);
    }


    public MutableLiveData<List<NsdServiceInfo>> getLiveNsdList(){
        return nsdFinalRepository.getLiveNsdDeviceList();
    }

    public MutableLiveData<NsdServiceInfo> getResolveNsdStatus() {
        return nsdFinalRepository.getNsdResolveStatus();
    }

    public void resolverNsdServiceFromRepository(NsdServiceInfo nsdServiceInfo){
        nsdFinalRepository.resolveNsdService(nsdServiceInfo, false);
    }

    public void passLinkedDeviceToRepo(List<TvInfo> tvInfos) {
        nsdFinalRepository.setLinkedTvInfoList(tvInfos);
    }

    public void refreshNsdService(){
        nsdFinalRepository.settingNsdDiscovery();;
    }

    public LiveData<Boolean> nsdDiscoveryStatus(){
        return nsdFinalRepository.getNsdDiscoveryStatus();
    }

    public void startDiscovery()
    {
        nsdFinalRepository.startNsdDiscovery();
    }

    public void stopDiscovery(){
        nsdFinalRepository.stopNsdDiscovery();
    }
}
