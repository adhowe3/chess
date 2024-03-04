package passoffTests.databaseTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;

public class SQLUserDaoTests {

    private UserDAO userDao;
    String username = "sampleUsername";
    String password = "samplePassword";
    String email = "sample@email.com";

    UserData u = new UserData(username, password, email);

    String username2 = "username2";
    String password2 = "password2";
    String email2 = "sample2@email.com";

    UserData u2 = new UserData(username2, password2, email2);

    @BeforeEach
    public void setUp() throws Exception{
        DatabaseManager.createDatabase();
        userDao = new MySQLUserDAO();
        userDao.clearUserData();
        userDao.add(u);
    }

    @Test
    public void testClear() throws DataAccessException{
        userDao.add(u2);
        userDao.clearUserData();
        // Verify that the user data is cleared by checking if there are any records left in the userTable
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM userTable");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            // Get the count of records in the userTable
            int count = 0;
            while(resultSet.next()) {
                count++;
            }
            assertEquals(0, count, "User data should be cleared");
        } catch (SQLException e) {
            fail("Failed to execute SQL query: " + e.getMessage());
        }
    }

    @Test
    public void testGetUser() throws DataAccessException {
        // Add a user to the database
        UserData userToAdd = new UserData("testUser", "testPassword", "test@example.com");
        userDao.add(userToAdd);

        // Retrieve the user from the database
        UserData retrievedUser = userDao.getUser("testUser");

        // Verify that the retrieved user is not null and has the correct attributes
        assertNotNull(retrievedUser, "Retrieved user should not be null");
        assertEquals("testUser", retrievedUser.getUsername(), "Username should match");
        assertEquals("testPassword", retrievedUser.getPassword(), "Password should match");
        assertEquals("test@example.com", retrievedUser.getEmail(), "Email should match");
    }

    @Test
    public void testGetUserNotFound() throws DataAccessException {
        // Attempt to retrieve a user that does not exist
        UserData retrievedUser = userDao.getUser("nonExistentUser");

        // Verify that the retrieved user is null
        assertNull(retrievedUser, "Retrieved user should be null");
    }


    @Test
    public void testAdd() throws DataAccessException {
        // Create a new user to add to the database
        UserData userToAdd = new UserData("testUser", "testPassword", "test@example.com");

        // Add the user to the database
        userDao.add(userToAdd);

        // Retrieve the user data from the database using SQL query
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT username, password, email FROM userTable WHERE username = ?"
             )) {
            preparedStatement.setString(1, "testUser");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Verify that the user data exists in the database and matches the original data
                assertTrue(resultSet.next(), "User data should exist in the database");
                assertEquals("testUser", resultSet.getString("username"), "Username should match");
                assertEquals("testPassword", resultSet.getString("password"), "Password should match");
                assertEquals("test@example.com", resultSet.getString("email"), "Email should match");
            }
        } catch (SQLException e) {
            fail("Failed to execute SQL query: " + e.getMessage());
        }
    }

    @Test
    public void testAddDuplicateUsername() {
        // Verify that adding the duplicate user throws a DataAccessException
        assertThrows(DataAccessException.class, () -> userDao.add(u), "Adding duplicate user should throw exception");
    }

    @Test
    public void testGetAll() throws DataAccessException {
        userDao.clearUserData();
        // Add some test data to the database
        UserData user1 = new UserData("user1", "password1", "user1@example.com");
        UserData user2 = new UserData("user2", "password2", "user2@example.com");
        userDao.add(user1);
        userDao.add(user2);

        // Retrieve all user data from the database
        ArrayList<UserData> allUserData = userDao.getAll();

        // Verify that the number of retrieved users matches the number of added users
        assertEquals(2, allUserData.size(), "Number of retrieved users should match number of added users");

        // Verify the details of each retrieved user
        for (UserData userData : allUserData) {
            if (userData.getUsername().equals("user1")) {
                assertEquals("password1", userData.getPassword(), "Password for user1 should match");
                assertEquals("user1@example.com", userData.getEmail(), "Email for user1 should match");
            } else if (userData.getUsername().equals("user2")) {
                assertEquals("password2", userData.getPassword(), "Password for user2 should match");
                assertEquals("user2@example.com", userData.getEmail(), "Email for user2 should match");
            } else {
                fail("Unexpected username retrieved: " + userData.getUsername());
            }
        }
    }

    @Test
    public void testGetAllNegative() {
        try {
            // Drop the authTable or delete the entire database
            dropUserTable();
            assertThrows(DataAccessException.class, userDao::getAll, "Method should throw DataAccessException");
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("Exception occurred during test: " + e.getMessage());
        }
    }

    private void dropUserTable() throws SQLException, DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            // Execute SQL command to drop the table or delete the database
            statement.executeUpdate("DROP TABLE IF EXISTS userTable");
        }
        catch(SQLException e){
            throw new DataAccessException("Did not drop");
        }
    }

} // END CLASS
