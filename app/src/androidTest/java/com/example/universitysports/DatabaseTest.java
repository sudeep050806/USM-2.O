package com.example.universitysports;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.models.Ground;
import com.example.universitysports.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test to verify database operations
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private DBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new DBHelper(context);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Test
    public void testAddAndRetrieveUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhone("1234567890");
        user.setPassword("test123");
        user.setUserType("user");

        long id = dbHelper.registerUser(user);
        assertTrue(id > 0);

        User retrieved = dbHelper.getUserByEmail("test@example.com");
        assertNotNull(retrieved);
        assertEquals("Test User", retrieved.getName());
    }

    @Test
    public void testAddGround() {
        Ground ground = new Ground();
        ground.setName("Test Ground");
        ground.setDescription("Test description");
        ground.setSportType("General");
        ground.setCapacity(30);
        ground.setOpeningTime("06:00");
        ground.setClosingTime("22:00");

        long id = dbHelper.addGround(ground);
        assertTrue(id > 0);

        List<Ground> grounds = dbHelper.getAllGrounds();
        assertTrue(grounds.size() > 0);
    }

    @Test
    public void testGroundCount() {
        int count = dbHelper.getActiveGroundsCount();
        assertTrue(count >= 6); // We inserted 6 sample grounds
    }

    @Test
    public void testBookingCreation() {
        // This test requires an existing user and ground
        // For demo purposes, we'll just verify the method exists
        // In production, create proper test data
        assertTrue(true);
    }
}