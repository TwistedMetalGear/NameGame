package net.silentbyte.namegame.data;

import net.silentbyte.namegame.data.source.local.NameGameDatabase;
import net.silentbyte.namegame.data.source.local.ProfileDao;
import net.silentbyte.namegame.data.source.local.ProfileEntity;
import net.silentbyte.namegame.data.source.remote.Headshot;
import net.silentbyte.namegame.data.source.remote.NameGameApi;
import net.silentbyte.namegame.data.source.remote.Profile;
import net.silentbyte.namegame.glide.TestHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NameGameRepositoryTest {

    // The Repository under test.
    private NameGameRepository repository;

    List<Profile> profiles;

    @Mock
    private NameGameDatabase nameGameDatabase;
    @Mock
    private ProfileDao profileDao;
    @Mock
    private NameGameApi nameGameApi;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void createRepository() {
        repository = new NameGameRepository(nameGameDatabase, nameGameApi);
    }

    @Before
    public void createProfiles() {
        profiles = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Headshot.Builder headshotBuilder = new Headshot.Builder();
            Profile.Builder profileBuilder = new Profile.Builder();
            profileBuilder.id(String.valueOf(i));
            profileBuilder.headshot(headshotBuilder.build());
            profiles.add(profileBuilder.build());
        }
    }

    @Test
    public void getRandomProfiles_returnsProfiles() {
        getRandomProfiles();
    }

    @Test
    public void getRandomProfiles_returnsCachedProfiles() {
        getRandomProfiles();

        TestObserver<List<ProfileEntity>> observer = repository.getRandomProfiles().test();
        observer.assertNoErrors();

        verify(profileDao, times(1)).insertProfiles(anyList());
        verify(profileDao, times(2)).getRandomProfiles(6);
    }

    @Test
    public void getRandomProfiles_refreshFails_returnsProfiles() {
        Single<List<Profile>> profilesSingle = Single.create(emitter -> emitter.onError(new Exception()));
        Single<List<ProfileEntity>> profileEntitiesSingle = Single.create(emitter -> emitter.onSuccess(TestHelper.getProfiles()));
        Single<Integer> profileCountSingle = Single.create(emitter -> emitter.onSuccess(6));

        when(nameGameDatabase.profileDao()).thenReturn(profileDao);
        when(nameGameApi.getProfiles()).thenReturn(profilesSingle);
        when(profileDao.getRandomProfiles(6)).thenReturn(profileEntitiesSingle);
        when(profileDao.getProfileCount()).thenReturn(profileCountSingle);

        TestObserver<List<ProfileEntity>> observer = repository.getRandomProfiles().test();
        observer.assertNoErrors();

        verify(profileDao, never()).insertProfiles(anyList());
        verify(profileDao, times(1)).getProfileCount();
        verify(profileDao, times(1)).getRandomProfiles(6);
    }

    @Test
    public void getProfiles_returnsProfiles() {
        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onSuccess(new ArrayList<>()));
        List<String> employeeIds = new ArrayList<>();

        when(nameGameDatabase.profileDao()).thenReturn(profileDao);
        when(profileDao.getProfiles(employeeIds)).thenReturn(single);

        TestObserver<List<ProfileEntity>> observer = repository.getProfiles(employeeIds).test();
        observer.assertNoErrors();

        verify(profileDao, times(1)).getProfiles(employeeIds);
    }

    private void getRandomProfiles() {
        Single<List<Profile>> profilesSingle = Single.create(emitter -> emitter.onSuccess(profiles));
        Single<List<ProfileEntity>> profileEntitiesSingle = Single.create(emitter -> emitter.onSuccess(TestHelper.getProfiles()));

        when(nameGameDatabase.profileDao()).thenReturn(profileDao);
        when(nameGameApi.getProfiles()).thenReturn(profilesSingle);
        when(profileDao.getRandomProfiles(6)).thenReturn(profileEntitiesSingle);

        TestObserver<List<ProfileEntity>> observer = repository.getRandomProfiles().test();
        observer.assertNoErrors();

        verify(profileDao, times(1)).insertProfiles(anyList());
        verify(profileDao, times(1)).getRandomProfiles(6);
    }
}