package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import repository.TvRemoteRepository;

public class TvRemoteViewModel extends AndroidViewModel
{
    private TvRemoteRepository tvRemoteRepository ;

    public TvRemoteViewModel(@NonNull Application application) {
        super(application);
        tvRemoteRepository = new TvRemoteRepository();
    }

    public void sentNsdMessageFromRepository(String host, int port, String nsdMessage) {
        tvRemoteRepository.sendMessageFromNSD(host, port, nsdMessage);
    }

    public MutableLiveData<Boolean> getNsdMessageSentStatus() {
        return tvRemoteRepository.getNsdMessageSendStatus();
    }
}
