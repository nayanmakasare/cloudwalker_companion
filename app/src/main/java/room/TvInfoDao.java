package room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TvInfoDao
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNewDevice(TvInfo tvInfo);

    @Delete
    void deleteLinkedDevice(TvInfo tvInfo);

    @Update
    void updateLinkedDevice(TvInfo tvInfo);

    @Query("SELECT * FROM tvInfo_table")
    LiveData<List<TvInfo>> getAllLinkedDevice();

    @Query("DELETE FROM tvInfo_table")
    void deleteAllDevice();

    @Query("SELECT * FROM tvInfo_table WHERE emac = :tvEmac LIMIT 1")
    TvInfo findTvInfoByEmac(String tvEmac);
}
