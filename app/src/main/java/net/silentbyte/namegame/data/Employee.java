package net.silentbyte.namegame.data;

public interface Employee {

    String getId();

    String getFirstName();

    String getLastName();

    String getPictureUrl();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
