package passoffTests.databaseTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.sql.PreparedStatement;

public class SQLAuthDaoTests {

    private AuthDAO authDao;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize the database
        DatabaseManager.createDatabase();
        // Initialize the DAO
        authDao = new MySQLAuthDAO();

        AuthData testData = new AuthData("sampleAuthToken", "sampleUsername");
        authDao.add(testData);
    }

    @Test
    @Order(1)
    public void testDatabaseInitialization() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Check if authTable exists
            ResultSet authTableResultSet = metaData.getTables(null, null, "authTable", null);
            Assertions.assertTrue(authTableResultSet.next());

            // Verify the structure of authTable
            ResultSet authTableColumnsResultSet = metaData.getColumns(null, null, "authTable", null);
            Set<String> authTableColumnNames = new HashSet<>();
            while (authTableColumnsResultSet.next()) {
                authTableColumnNames.add(authTableColumnsResultSet.getString("COLUMN_NAME"));
            }
            Assertions.assertTrue(authTableColumnNames.contains("id"));
            Assertions.assertTrue(authTableColumnNames.contains("username"));
            Assertions.assertTrue(authTableColumnNames.contains("authToken"));

            // Repeat the above steps for userTable and gameTable

        } catch (SQLException e) {
            Assertions.fail("Database initialization test failed: " + e.getMessage());
        }
    }

    @Test
    @Order (2)
    public void testAdd() throws DataAccessException {
        // Create an instance of AuthData with sample data
        AuthData expectedData = new AuthData("anotherAuthToken", "anotherUsername");

        // Call the add method to insert the sample data into the database
        authDao.add(expectedData);

        // Retrieve the inserted data from the database
        AuthData actualData = getAuthDataFromDatabase("anotherUsername");

        // Verify that the retrieved data matches the expected data
        assert actualData != null;
        Assertions.assertEquals(expectedData.getAuthToken(), actualData.getAuthToken());
        Assertions.assertEquals(expectedData.getUsername(), actualData.getUsername());
    }

    private AuthData getAuthDataFromDatabase(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT authToken, username FROM authTable WHERE username = ?"
             )) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String authToken = resultSet.getString("authToken");
                    String retrievedUsername = resultSet.getString("username");
                    return new AuthData(authToken, retrievedUsername);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve AuthData from database" + e.getMessage());
        }
        return null; // Return null if no data is found (you can handle this case differently if needed)
    }

    @Test
    public void testGetDataFromToken() throws DataAccessException {
        // Call the method to retrieve AuthData for the sampleAuthToken
        AuthData retrievedData = authDao.getDataFromToken("sampleAuthToken");
        // Verify that the retrieved data matches the expected data
        Assertions.assertNotNull(retrievedData); // Ensure that retrievedData is not null
        Assertions.assertEquals("sampleAuthToken", retrievedData.getAuthToken());
        Assertions.assertEquals("sampleUsername", retrievedData.getUsername());
    }

    @Test
    public void testDelete() throws DataAccessException {
        // Call the delete method to delete the sampleAuthToken
        boolean deletionResult = authDao.delete("sampleAuthToken");

        // Verify that deletion was successful
        Assertions.assertTrue(deletionResult, "Deletion should be successful");

        // Verify that the authToken no longer exists in the database
        AuthData retrievedData = authDao.getDataFromToken("sampleAuthToken");
        Assertions.assertNull(retrievedData, "AuthData should be null after deletion");
    }





}
