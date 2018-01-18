package net.silentbyte.namegame.data.source.remote;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Retrofit API providing methods to retrieve remote data from the WillowTree server.
 * For the purpose of this app, we are only concerned with retrieving a list of profiles.
 */
public interface NameGameApi {

    String BASE_URL = "https://willowtreeapps.com/api/v1.0/";

    @GET("profiles")
    Single<List<Profile>> getProfiles();
}
