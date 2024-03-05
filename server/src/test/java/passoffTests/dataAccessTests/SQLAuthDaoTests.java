package passoffTests.dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

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
        authDao.add(new AuthData("fakeAuthToken", "fakeUser"));
        authDao.clearAuthData();

        // Verify that the user data is cleared by checking if there are any records left in the userTable
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM authTable");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Get the count of records in the userTable
            int count = 0;
            while(resultSet.next()) {
                count++;
            }

            Assertions.assertEquals(0, count, "Authdata should be cleared");
        } catch (SQLException e) {
            Assertions.fail("Failed to execute SQL query: " + e.getMessage());
        }
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
        AuthData retrievedData = authDao.getDataFromToken(sampleAuthToken);
        Assertions.assertNotNull(retrievedData); // Ensure that retrievedData is not null
        Assertions.assertEquals(sampleAuthToken, retrievedData.getAuthToken());
        Assertions.assertEquals(sampleUserName, retrievedData.getUsername());
    }

    @Test
    @Order(5)
    public void testGetDataFromTokenNegative() throws DataAccessException {
        AuthData retrievedData = authDao.getDataFromToken("tokenNotInDatabase");
        Assertions.assertNull(retrievedData, "retrievedData should have been null"); // Ensure that retrievedData is null
    }

    @Test
    @Order(6)
    public void testDelete() throws DataAccessException {
        boolean deletionResult = authDao.delete(sampleAuthToken);
        Assertions.assertTrue(deletionResult, "Deletion should be successful");
        AuthData retrievedData = authDao.getDataFromToken(sampleAuthToken);
        Assertions.assertNull(retrievedData, "AuthData should be null after deletion");
    }

    @Test
    @Order(7)
    public void testDeleteNegative() throws DataAccessException{
        boolean deletionResult = authDao.delete("authTokenNotInDatabase");
        Assertions.assertFalse(deletionResult, "Deletion should be unsuccessful");
    }

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

    private void dropAuthTable() throws SQLException, DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS authTable");
        }
        catch(SQLException e){
            throw new DataAccessException("Did not drop");
        }
    }











}
