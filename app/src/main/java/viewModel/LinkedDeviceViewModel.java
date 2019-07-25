package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import model.TvInfo;
import repository.LinkedDeviceRepository;

public class LinkedDeviceViewModel extends AndroidViewModel
{
    LinkedDeviceRepository linkedDeviceRepository;

    public LinkedDeviceViewModel(@NonNull Application application) {
        super(application);
        linkedDeviceRepository = new LinkedDeviceRepository(application);
    }


    public MutableLiveData<List<TvInfo>> getLiveLinkedDevice(){
        return linkedDeviceRepository.getLiveLinkedDevice();
    }

    public void removeLinkedDeviceFromRepository(TvInfo tvInfo) {
        linkedDeviceRepository.removedLinkedDevice(tvInfo);
    }

    public MutableLiveData<Boolean> getRemoveLinkDeviceStatus(){
        return  linkedDeviceRepository.getRemovedDeviceStatus();
    }
}
