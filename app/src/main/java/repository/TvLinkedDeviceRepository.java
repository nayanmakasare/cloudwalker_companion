package repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import room.TvInfo;
import room.TvInfoDao;
import room.TvInfoDatabase;

public class TvLinkedDeviceRepository
{
    private TvInfoDao tvInfoDao;
    private static final String TAG = "TvLinkedDeviceRepositor";
    private LiveData<List<TvInfo>> allLinkedDevice ;
    private static PreferenceManager mPreferenceManager;

    public TvLinkedDeviceRepository(Context context) {
        TvInfoDatabase tvInfoDatabase =    TvInfoDatabase.getInstance(context);
        tvInfoDao =tvInfoDatabase.tvInfoDao();
        allLinkedDevice = tvInfoDao.getAllLinkedDevice();
        mPreferenceManager = new PreferenceManager(context);
    }


    public void  insert(TvInfo tvInfo)
    {
        new InsertDeviceAsyncTask(tvInfoDao).execute(tvInfo);
    }

    public void delete(TvInfo tvInfo)
    {
        Log.d(TAG, "delete: ");
        new DeleteDeviceAsyncTask(tvInfoDao).execute(tvInfo);
    }

    public void update(TvInfo tvInfo)
    {
        new UpdateDeviceAsyncTask(tvInfoDao).execute(tvInfo);
    }

    public void deleteAllDevice()
    {
        new DeleteAllDeviceAsyncTask(tvInfoDao).execute();
    }

    public LiveData<List<TvInfo>> getAllLinkedDevice() {
        return allLinkedDevice;
    }


    private static class InsertDeviceAsyncTask extends AsyncTask<TvInfo , Void, Void>
    {
        private TvInfoDao tvInfoDao;

        private InsertDeviceAsyncTask(TvInfoDao tvInfoDao)
        {
            this.tvInfoDao = tvInfoDao;
        }

        @Override
        protected Void doInBackground(TvInfo... tvInfos) {
            tvInfoDao.insertNewDevice(tvInfos[0]);
            return null;
        }
    }

    private static class DeleteDeviceAsyncTask extends AsyncTask<TvInfo , Void, Void>
    {
        private TvInfoDao tvInfoDao;

        private DeleteDeviceAsyncTask(TvInfoDao tvInfoDao) {
            this.tvInfoDao = tvInfoDao;
        }

        @Override
        protected Void doInBackground(final TvInfo... tvInfos)
        {
            try 
            {
               Response<ResponseBody> response = new Retrofit.Builder()
                        .baseUrl("http://192.168.1.222:5080/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(MyProfileInterface.class)
                        .removeTvDevice(tvInfos[0], mPreferenceManager.getGoogleId(), tvInfos[0].getEmac()).execute();
                Log.d(TAG, "doInBackground: response "+response.code());
               if(response.code() == 200) {
                   Log.d(TAG, "doInBackground: deleting");
                   tvInfoDao.deleteLinkedDevice(tvInfos[0]);
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class UpdateDeviceAsyncTask extends AsyncTask<TvInfo , Void, Void>
    {
        private TvInfoDao tvInfoDao;

        private UpdateDeviceAsyncTask(TvInfoDao tvInfoDao)
        {
            this.tvInfoDao = tvInfoDao;
        }

        @Override
        protected Void doInBackground(TvInfo... tvInfos) {
            tvInfoDao.updateLinkedDevice(tvInfos[0]);
            return null;
        }
    }

    private static class DeleteAllDeviceAsyncTask extends AsyncTask<Void , Void, Void>
    {
        private TvInfoDao tvInfoDao;

        private DeleteAllDeviceAsyncTask(TvInfoDao tvInfoDao)
        {
            this.tvInfoDao = tvInfoDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tvInfoDao.deleteAllDevice();
            return null;
        }
    }
}
