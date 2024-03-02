package passoffTests.databaseTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

            Assertions.assertEquals(0, count, "User data should be cleared");
        } catch (SQLException e) {
            Assertions.fail("Failed to execute SQL query: " + e.getMessage());
        }

    }

}
