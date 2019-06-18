package viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import model.MovieTile;
import repository.MovieBoxRepository;

public class MovieBoxViewModel extends AndroidViewModel
{
    MovieBoxRepository movieBoxRepository;

    public MovieBoxViewModel(@NonNull Application application) {
        super(application);
        movieBoxRepository = new MovieBoxRepository(application);
    }

    public MutableLiveData<List<MovieTile>> getLiveMovieTileFromRepo() {
        return movieBoxRepository.getLiveMovieTile();
    }

    public void sendPlayMovieNsdMessage(String host, int port, String nsdMessage) {
        movieBoxRepository.sendMessageFromNSD(host, port, nsdMessage);
    }

    public MutableLiveData<Boolean> getPlayMovieNsdMessage() {
        return movieBoxRepository.getNsdMessageSendStatus();
    }
}
