package net.silentbyte.namegame.data;

import net.silentbyte.namegame.data.source.local.ProfileEntity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeTest {

    private ProfileEntity employee;

    @Before
    public void createEmployee() {
        employee = new ProfileEntity();
        employee.setId("1");
        employee.setFirstName("Walter");
        employee.setLastName("White");
        employee.setPictureUrl("http://url.to/walter_white.png");
    }

    @Test
    public void getFullName() {
        assertEquals("Walter White", employee.getFullName());
    }
}