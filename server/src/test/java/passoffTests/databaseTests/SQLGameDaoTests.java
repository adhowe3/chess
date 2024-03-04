package passoffTests.databaseTests;

import chess.*;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.GameDAO;
import dataAccess.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDaoTests {

    private GameDAO gameDao;

    @BeforeEach
    public void setUp() throws DataAccessException{
        DatabaseManager.createDatabase();
        gameDao = new MySQLGameDAO();
        gameDao.clearGameData();
    }

    @Test
    public void testClearGameData() throws DataAccessException {
        // Insert some sample data into the gameTable
        insertSampleData();
        // Clear the gameTable
        gameDao.clearGameData();
        // Verify that the gameTable is empty
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM gameTable"
             )) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Verify that the count of rows is 0 after truncating the table
                assertFalse(resultSet.next(), "GameTable should be empty after clearing");
            }
        } catch (SQLException e) {
            fail("Failed to execute SQL query: " + e.getMessage());
        }
    }

    private void insertSampleData() {
        // Sample data to be inserted into the gameTable
        GameData gameData1 = new GameData(1,null,null,"game1", new ChessGame());
        GameData gameData2 = new GameData(2,null,null,"game2", new ChessGame());

        // Insert the sample data into the gameTable
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO gameTable (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)"
             )) {
            // Insert the first sample data
            preparedStatement.setString(1, gameData1.getWhiteUsername());
            preparedStatement.setString(2, gameData1.getBlackUsername());
            preparedStatement.setString(3, gameData1.getGameName());
            preparedStatement.setString(4, new Gson().toJson(gameData1.getGame()));
            preparedStatement.executeUpdate();

            // Insert the second sample data
            preparedStatement.setString(1, gameData2.getWhiteUsername());
            preparedStatement.setString(2, gameData2.getBlackUsername());
            preparedStatement.setString(3, gameData2.getGameName());
            preparedStatement.setString(4, new Gson().toJson(gameData2.getGame()));
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            fail("Failed to insert sample data into gameTable: " + e.getMessage());
        }
    }


    @Test
    public void testAdd() throws DataAccessException, SQLException, InvalidMoveException {
        // Create a sample GameData object to add to the database\
        int gameID = 1;
        String gameName = "extraSampleGameName";
        String whiteUsr = "allan";
        String blackUsr = null;
        ChessGame sentGame = new ChessGame();
        GameData gameDataToAdd = new GameData(gameID, whiteUsr, blackUsr, gameName, sentGame);

        // Add the GameData object to the database
        gameDao.add(gameDataToAdd);

        // Retrieve the inserted data from the database
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameID = ?"
             )) {
            preparedStatement.setInt(1, gameID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Verify that the data exists in the database
                assertTrue(resultSet.next(), "Data should exist in the database");

                // Verify that the retrieved data matches the expected data
                assertEquals(gameName, resultSet.getString("gameName"), "GameName should match");
                assertEquals(whiteUsr, resultSet.getString("whiteUsername"), "whiteUsername should match");
                assertEquals(blackUsr, resultSet.getString("blackUsername"), "blackUsername should match");

                // Deserialize the JSON string back to ChessGame object for comparison
                String gameJson = resultSet.getString("game");
                ChessGame retrievedGame = new Gson().fromJson(gameJson, ChessGame.class);
                // Ensure that the retrieved ChessGame object is not null
                assertNotNull(retrievedGame, "Retrieved game should not be null");
                assertEquals(sentGame, retrievedGame, "Game objects should match");
                // Ensure no other data exists in the database
                assertFalse(resultSet.next(), "Only one row should be retrieved");
            }
        } catch (SQLException e) {
            fail("Failed to execute SQL query: " + e.getMessage());
        }
    }

    @Test
    public void testAddTableNotExists() {
        // Drop the gameTable to simulate non-existence
        dropGameTable();

        int gameID = 1;
        String gameName = "sampleGameName";
        ChessGame sentGame = new ChessGame();
        GameData gameDataToAdd = new GameData(gameID,"white", "black", gameName, sentGame);

        // Verify that adding the GameData object throws a DataAccessException
        assertThrows(DataAccessException.class, () -> gameDao.add(gameDataToAdd),
                "Adding to non-existent table should throw DataAccessException");
    }

    // Helper method to drop the gameTable
    private void dropGameTable() {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DROP TABLE IF EXISTS gameTable"
             )) {
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            fail("Failed to drop gameTable: " + e.getMessage());
        }
    }

    @Test
    public void testGetGameDataFromID() throws DataAccessException {
        // Insert test data into the database
        int insertedGameID = 1;
        GameData testData = new GameData(1, "whiteUser", "blackUser", "TestGame", new ChessGame());
        gameDao.add(testData);

        // Retrieve the inserted data from the database using getGameDataFromID
        GameData retrievedData = gameDao.getGameDataFromID(insertedGameID);

        // Verify that retrievedData matches testData
        assertNotNull(retrievedData, "Retrieved game data should not be null");
        assertEquals(insertedGameID, retrievedData.getGameID(), "GameID should match");
        assertEquals(testData.getWhiteUsername(), retrievedData.getWhiteUsername(), "White username should match");
        assertEquals(testData.getBlackUsername(), retrievedData.getBlackUsername(), "Black username should match");
        assertEquals(testData.getGameName(), retrievedData.getGameName(), "Game name should match");
    }

    @Test
    public void testGetGameDataFromIDNegative() throws DataAccessException{
        // Attempt to retrieve game data by ID
        int badGameID = 100;
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameDao.getGameDataFromID(badGameID),
                "Method should throw DataAccessException due to SQL error");

        // Verify that the exception message is as expected
        assertEquals("Error: bad request", exception.getMessage(), "Exception message should match");
    }

    @Test
    public void testUpdateWhiteUsername() throws DataAccessException {
        // Insert a test game data into the database
        GameData testData = new GameData(1, "white", null, "gameName", new ChessGame());
        gameDao.add(testData);

        // Update whiteUsername for the inserted game
        String newWhiteUsername = "newWhiteUser";
        gameDao.updateWhiteUsername(1, newWhiteUsername);

        // Retrieve the updated game data
        GameData updatedData = gameDao.getGameDataFromID(1);

        // Verify that the whiteUsername has been updated
        assertNotNull(updatedData, "Retrieved game data should not be null");
        assertEquals(newWhiteUsername, updatedData.getWhiteUsername(), "White username should match the updated value");
    }

    @Test
    public void testUpdateWhiteUsernameNegative() {
        // Attempt to update whiteUsername for an incorrect gameID
        int incorrectGameID = -1; // assuming this gameID does not exist
        String newWhiteUsername = "newWhiteUser";

        // Verify that attempting to update the whiteUsername throws the correct exception
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameDao.updateWhiteUsername(incorrectGameID, newWhiteUsername),
                "Method should throw DataAccessException for incorrect gameID");
        assertEquals("Error: bad request", exception.getMessage(), "Exception message should match");
    }





}
