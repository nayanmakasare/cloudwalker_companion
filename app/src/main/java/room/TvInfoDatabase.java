package room;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import api.MyProfileInterface;
import appUtils.PreferenceManager;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Database(entities = {TvInfo.class}, version = 1, exportSchema = false)
public abstract  class TvInfoDatabase extends RoomDatabase
{
        private static TvInfoDatabase instance ;
        private static final String TAG = "TvInfoDatabase";
        public abstract TvInfoDao tvInfoDao();
        private static PreferenceManager preferenceManager;

        public static synchronized  TvInfoDatabase getInstance(Context context) {
            preferenceManager = new PreferenceManager(context);
            if(instance == null) {
                instance = Room
                        .databaseBuilder(context.getApplicationContext(), TvInfoDatabase.class, "tvInfo_database")
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build();
            }
            return instance;
        }

        private static  RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                new PopulateAsyncTask(instance).execute();
            }
        };


        private static class PopulateAsyncTask extends AsyncTask<Void , Void, Void>
        {
            private TvInfoDao tvInfoDao ;

            private PopulateAsyncTask(TvInfoDatabase tvInfoDatabase)
            {
                tvInfoDao = tvInfoDatabase.tvInfoDao();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Response<List<TvInfo>> linkedTvDevices = new Retrofit.Builder()
                            .baseUrl("http://192.168.1.222:5080/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(MyProfileInterface.class)
                            .getLinkDevices(preferenceManager.getGoogleId())
                            .execute();

                    if(linkedTvDevices.code() == 200 && linkedTvDevices.body().size() > 0)
                    {
                        Log.d(TAG, "onResponse: got some thing");
                        for(TvInfo tvInfo : linkedTvDevices.body()) {
                            tvInfoDao.insertNewDevice(tvInfo);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                        .enqueue(new retrofit2.Callback<List<TvInfo>>() {
//                            @Override
//                            public void onResponse(Call<List<TvInfo>> call, Response<List<TvInfo>> response) {
//                                Log.d(TAG, "onResponse: "+response.code());
//                                if(response.code() == 200){
//                                    if(response.body().size() > 0) {
//                                        Log.d(TAG, "onResponse: got some thing");
//                                        for(TvInfo tvInfo : response.body()) {
//                                            tvInfoDao.insertNewDevice(tvInfo);
//                                        }
//                                    }else {
//                                        Log.d(TAG, "onResponse: not got any");
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<List<TvInfo>> call, Throwable t) {
//                                Log.d(TAG, "onFailure: "+t.getMessage());
//                            }
//                        });

                return null;
            }
        }
}
