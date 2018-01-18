package net.silentbyte.namegame.data.source.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import net.silentbyte.namegame.data.Employee;

/**
 * Entity class representing the profiles table which stores individual employee profiles.
 * The profiles table is simplified since this app only requires names and pictures.
 * If this were a production app, we might wish to store the entire set of profile data in the table.
 */

@Entity(tableName = "profiles")
public class ProfileEntity implements Employee {

    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "picture_url")
    private String pictureUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
