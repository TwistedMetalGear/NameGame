package net.silentbyte.namegame.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfiles(List<ProfileEntity> profiles);

    @Query("SELECT * FROM profiles")
    Single<List<ProfileEntity>> getProfiles();

    @Query("SELECT * FROM profiles WHERE id IN (:profileIds)")
    Single<List<ProfileEntity>> getProfiles(List<String> profileIds);

    @Query("SELECT * FROM profiles WHERE id IN (SELECT id FROM profiles ORDER BY RANDOM() LIMIT :limit)")
    Single<List<ProfileEntity>> getRandomProfiles(int limit);

    @Query("SELECT COUNT(*) FROM profiles")
    Single<Integer> getProfileCount();
}
