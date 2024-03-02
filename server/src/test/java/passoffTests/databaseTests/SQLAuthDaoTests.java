package passoffTests.databaseTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLAuthDaoTests {

    private static AuthDAO authDao;
    String sampleAuthToken = "SampleAuthToken";
    String sampleUserName = "sampleUserName";
    String anotherAuthToken = "anotherAuthToken";
    String anotherUserName = "anotherUserName";

    @BeforeEach
    public void setUp() throws Exception {
        DatabaseManager.createDatabase();
        authDao = new MySQLAuthDAO();
        authDao.clearAuthData();
        AuthData testData = new AuthData(sampleAuthToken, sampleUserName);
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
    public void testClear() throws DataAccessException{

    }

    @Test
    @Order(2)
    public void testAdd() throws DataAccessException {
        // Create an instance of AuthData with sample data
        AuthData expectedData = new AuthData(anotherAuthToken, anotherUserName);

        // Call the add method to insert the sample data into the database
        authDao.add(expectedData);

        // Retrieve the inserted data from the database
        AuthData actualData = getAuthDataFromDatabase(anotherUserName);

        // Verify that the retrieved data matches the expected data
        assert actualData != null;
        Assertions.assertEquals(expectedData.getAuthToken(), actualData.getAuthToken());
        Assertions.assertEquals(expectedData.getUsername(), actualData.getUsername());
    }

    @Test
    @Order(3)
    public void testNegativeAdd(){
        AuthData reAddedData = new AuthData(sampleAuthToken, sampleUserName);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> authDao.add(reAddedData));
        Assertions.assertEquals("Error: authToken taken", exception.getMessage(), "Thrown error did not match expected error");
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
    @Order(4)
    public void testGetDataFromToken() throws DataAccessException {
        // Call the method to retrieve AuthData for the sampleAuthToken
        AuthData retrievedData = authDao.getDataFromToken(sampleAuthToken);
        // Verify that the retrieved data matches the expected data
        Assertions.assertNotNull(retrievedData); // Ensure that retrievedData is not null
        Assertions.assertEquals(sampleAuthToken, retrievedData.getAuthToken());
        Assertions.assertEquals(sampleUserName, retrievedData.getUsername());
    }

    @Test
    @Order(5)
    public void testGetDataFromTokenNegative() throws DataAccessException {
        // Call the method to retrieve AuthData for the sampleAuthToken
        AuthData retrievedData = authDao.getDataFromToken("tokenNotInDatabase");
        // Verify that the retrieved data matches the expected data
        Assertions.assertNull(retrievedData, "retrievedData should have been null"); // Ensure that retrievedData is null
    }

    @Test
    @Order(6)
    public void testDelete() throws DataAccessException {
        // Call the delete method to delete the sampleAuthToken
        boolean deletionResult = authDao.delete(sampleAuthToken);

        // Verify that deletion was successful
        Assertions.assertTrue(deletionResult, "Deletion should be successful");

        // Verify that the authToken no longer exists in the database
        AuthData retrievedData = authDao.getDataFromToken(sampleAuthToken);
        Assertions.assertNull(retrievedData, "AuthData should be null after deletion");
    }

    @Test
    @Order(7)
    public void testDeleteNegative() throws DataAccessException{
        boolean deletionResult = authDao.delete("authTokenNotInDatabase");
        Assertions.assertFalse(deletionResult, "Deletion should be unsuccessful");
    }

    // Positive case: Testing successful retrieval of all data
    @Test
    @Order(8)
    public void testGetAllPositive() throws DataAccessException {
        // Insert sample data into the database
        AuthData testData1 = new AuthData("authToken1", "username1");
        AuthData testData2 = new AuthData("authToken2", "username2");
        authDao.add(testData1);
        authDao.add(testData2);

        // Retrieve all data from the database
        ArrayList<AuthData> authDataList = authDao.getAll();

        // Verify that the list is not null and contains the correct number of elements
        Assertions.assertNotNull(authDataList, "List should not be null");
        Assertions.assertEquals(3, authDataList.size(), "List should contain 3 elements");

        // Verify the content of the retrieved data
        Assertions.assertTrue(authDataList.contains(testData1), "List should contain testData1");
        Assertions.assertTrue(authDataList.contains(testData2), "List should contain testData2");
    }

    // Negative case: Testing unsuccessful retrieval of data
    @Test
    @Order(9)
    public void testGetAllNegative() {
        try {
            // Drop the authTable or delete the entire database
            dropAuthTable();
            assertThrows(DataAccessException.class, authDao::getAll, "Method should throw DataAccessException");
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("Exception occurred during test: " + e.getMessage());
        }
    }

    // Method to drop the authTable (or delete the entire database)
    private void dropAuthTable() throws SQLException, DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            // Execute SQL command to drop the table or delete the database
            statement.executeUpdate("DROP TABLE IF EXISTS authTable");
        }
        catch(SQLException e){
            throw new DataAccessException("Did not drop");
        }
    }











}
