package net.silentbyte.namegame.glide;

import net.silentbyte.namegame.data.source.local.ProfileEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

public class TestHelper {

    public static List<ProfileEntity> getProfiles() {
        List<ProfileEntity> profiles = new ArrayList<>();

        ProfileEntity profile = new ProfileEntity();
        profile.setId("1");
        profile.setFirstName("Walter");
        profile.setLastName("White");
        profile.setPictureUrl("http://url.to/walter_white.png");
        profiles.add(profile);

        profile = new ProfileEntity();
        profile.setId("2");
        profile.setFirstName("Jack");
        profile.setLastName("Bauer");
        profile.setPictureUrl("http://url.to/jack_bauer.png");
        profiles.add(profile);

        profile = new ProfileEntity();
        profile.setId("3");
        profile.setFirstName("Sarah");
        profile.setLastName("Manning");
        profile.setPictureUrl("http://url.to/sarah_manning.png");
        profiles.add(profile);

        profile = new ProfileEntity();
        profile.setId("4");
        profile.setFirstName("Gregory");
        profile.setLastName("House");
        profile.setPictureUrl("http://url.to/gregory_house.png");
        profiles.add(profile);

        profile = new ProfileEntity();
        profile.setId("5");
        profile.setFirstName("Elliot");
        profile.setLastName("Alderson");
        profile.setPictureUrl("http://url.to/elliot_alderson.png");
        profiles.add(profile);

        profile = new ProfileEntity();
        profile.setId("6");
        profile.setFirstName("Dexter");
        profile.setLastName("Morgan");
        profile.setPictureUrl("http://url.to/dexter_morgan.png");
        profiles.add(profile);

        return profiles;
    }

    public static void initializeRxSchedulers() {
        Scheduler scheduler = new Scheduler() {
            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> scheduler);
        RxJavaPlugins.setInitIoSchedulerHandler(schedulerCallable -> scheduler);
    }
}
