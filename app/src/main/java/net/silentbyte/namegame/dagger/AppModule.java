package net.silentbyte.namegame.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import net.silentbyte.namegame.data.source.local.NameGameDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    NameGameDatabase provideNameGameDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
            NameGameDatabase.class, NameGameDatabase.DATABASE_NAME).build();
    }
}
