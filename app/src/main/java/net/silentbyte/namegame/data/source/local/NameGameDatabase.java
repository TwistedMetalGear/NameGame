package net.silentbyte.namegame.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {ProfileEntity.class}, version = 1)
public abstract class NameGameDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "namegame.db";

    public abstract ProfileDao profileDao();
}
