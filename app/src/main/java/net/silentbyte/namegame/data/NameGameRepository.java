package net.silentbyte.namegame.data;

import net.silentbyte.namegame.data.source.local.NameGameDatabase;
import net.silentbyte.namegame.data.source.local.ProfileEntity;
import net.silentbyte.namegame.data.source.remote.NameGameApi;
import net.silentbyte.namegame.data.source.remote.Profile;
import net.silentbyte.namegame.game.GameConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class NameGameRepository {

    private final NameGameDatabase nameGameDatabase; // Local database
    private final NameGameApi nameGameApi; // Remote API

    // Indicates that the local database should be updated with a fresh set of profiles
    // from the WillowTree server. Upon retrieval, this will be set to false and subsequent
    // retrievals will come from the local database.
    private boolean profilesStale = true;

    @Inject
    public NameGameRepository(NameGameDatabase nameGameDatabase, NameGameApi nameGameApi) {
        this.nameGameDatabase = nameGameDatabase;
        this.nameGameApi = nameGameApi;
    }

    public Single<List<ProfileEntity>> getRandomProfiles() {
        if (profilesStale) {
            return refreshProfiles()
                .onErrorResumeNext(throwable -> {
                    // Failed to refresh profiles, but we can still play if local database is populated.
                    return nameGameDatabase.profileDao().getProfileCount().flatMapCompletable(count ->
                        count > 0 ? Completable.complete() : Completable.error(throwable)
                    );
                })
                .andThen(nameGameDatabase.profileDao().getRandomProfiles(GameConstants.NUM_CHOICES))
                .doOnSuccess(profiles -> profilesStale = false);
        }
        else {
            return nameGameDatabase.profileDao().getRandomProfiles(GameConstants.NUM_CHOICES);
        }
    }

    public Single<List<ProfileEntity>> getProfiles(List<String> employeeIds) {
        return nameGameDatabase.profileDao().getProfiles(employeeIds);
    }

    /**
     * Returns a Completable that can be subscribed to in order to sync the local
     * database with the latest profile data from the WillowTree server.
     */
    private Completable refreshProfiles() {
        return nameGameApi.getProfiles()
            .flatMapCompletable(profiles -> {
                List<ProfileEntity> profileEntities = new ArrayList<>();

                for (Profile profile : profiles) {
                    ProfileEntity profileEntity = new ProfileEntity();

                    profileEntity.setId(profile.getId());
                    profileEntity.setFirstName(profile.getFirstName());
                    profileEntity.setLastName(profile.getLastName());
                    profileEntity.setPictureUrl(profile.getHeadshot().getUrl());

                    profileEntities.add(profileEntity);
                }

                nameGameDatabase.profileDao().insertProfiles(profileEntities);
                return Completable.complete();
            });
    }
}
