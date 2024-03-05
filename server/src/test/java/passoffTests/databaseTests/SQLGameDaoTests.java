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
import java.util.ArrayList;

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
    public void testUpdateWhiteUsernameNegative() throws DataAccessException {
        // Insert a test game data into the database
        GameData testData = new GameData(1, "white", null, "gameName", new ChessGame());
        gameDao.add(testData);
        // Attempt to update whiteUsername for an incorrect gameID
        int incorrectGameID = -1; // assuming this gameID does not exist
        String newWhiteUsername = "newWhiteUser";

        // Verify that attempting to update the whiteUsername throws the correct exception
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameDao.updateWhiteUsername(incorrectGameID, newWhiteUsername),
                "Method should throw DataAccessException for incorrect gameID");
        assertEquals("Error: bad request", exception.getMessage(), "Exception message should match");
    }

    @Test
    public void testUpdateBlackUsername() throws DataAccessException {
        // Insert a test game data into the database
        GameData testData = new GameData(1, null, null, "gameName", new ChessGame());
        gameDao.add(testData);

        // Update blackUsername for the inserted game
        String newBlackUsername = "newBlackUser";
        gameDao.updateBlackUsername(1, newBlackUsername);

        // Retrieve the updated game data
        GameData updatedData = gameDao.getGameDataFromID(1);

        // Verify that the blackUsername has been updated
        assertNotNull(updatedData, "Retrieved game data should not be null");
        assertEquals(newBlackUsername, updatedData.getBlackUsername(), "Black username should match the updated value");
    }

    @Test
    public void testUpdateBlackUsernameNegative() throws DataAccessException {
        // Insert a test game data into the database
        GameData testData = new GameData(1, null, "black", "gameName", new ChessGame());
        gameDao.add(testData);
        // Attempt to update blackUsername for an incorrect gameID
        int incorrectGameID = -1; // assuming this gameID does not exist
        String newBlackUsername = "newBlackUsername";

        // Verify that attempting to update the blackUsername throws the correct exception
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameDao.updateBlackUsername(incorrectGameID, newBlackUsername),
                "Method should throw DataAccessException for incorrect gameID");
        assertEquals("Error: bad request", exception.getMessage(), "Exception message should match");
    }

    @Test
    public void testNextGameID() throws DataAccessException {
        // Insert some test data to create gaps in the gameID sequence
        gameDao.add(new GameData(gameDao.nextGameID(), "WhitePlayer1", "BlackPlayer1", "Game1", new ChessGame())); // gameID: 1
        gameDao.add(new GameData(gameDao.nextGameID(), "WhitePlayer2", "BlackPlayer2", "Game2", new ChessGame())); // gameID: 2
        deleteGame(1); // Delete the first game, leaving a gap

        assertEquals(2, gameDao.nextGameID(), "Next gameID should auto increment");

        gameDao.add(new GameData(gameDao.nextGameID(), "WhitePlayer2", "BlackPlayer2", "Game2", new ChessGame())); // gameID: 2
        assertEquals(3, gameDao.nextGameID(), "Next gameID should be the smallest available unused ID");
    }

    @Test
    public void testNextGameIDNegative() throws DataAccessException {
        // Insert some test data to create gaps in the gameID sequence
        gameDao.add(new GameData(gameDao.nextGameID(), "WhitePlayer1", "BlackPlayer1", "Game1", new ChessGame())); // gameID: 1
        gameDao.add(new GameData(gameDao.nextGameID(), "WhitePlayer2", "BlackPlayer2", "Game2", new ChessGame())); // gameID: 2
        deleteGame(1); // Delete the first game, leaving a gap

        dropGameTable();

        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameDao.nextGameID(),
                "Method should throw SQL error");
    }

    public void deleteGame(int gameID) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM gameTable WHERE gameID = ?"
             )) {
            preparedStatement.setInt(1, gameID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to delete game from database - " + e.getMessage());
        }
    }

    @Test
    public void testGetAll() throws DataAccessException, InvalidMoveException {
        // Add some test data to the database
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        game2.makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(4,1)));
        game2.makeMove(new ChessMove(new ChessPosition(7,5), new ChessPosition(5,5)));
        gameDao.add(new GameData(1, "WhitePlayer1", "BlackPlayer1", "Game1", game1));
        gameDao.add(new GameData(2, "WhitePlayer2", "BlackPlayer2", "Game2", game2));

        // Retrieve all game data from the database
        ArrayList<GameData> allGameData = gameDao.getAll();

        // Verify that the size of the returned list matches the number of games added
        assertEquals(2, allGameData.size(), "Number of retrieved game data should match the number of games added");

        assertNotNull(allGameData.get(0)); // Ensure each game data object is not null
        assertEquals(allGameData.get(0).getGameID(), 1, "gameID 1 should match");
        assertEquals(allGameData.get(0).getGame(), game1, "game 1 should match");
        assertEquals(allGameData.get(1).getGame(), game2, "game 2 should match");
    }

    @Test
    public void testGetAllNegative() throws DataAccessException{
        // Add some test data to the database
        ChessGame game1 = new ChessGame();
        gameDao.add(new GameData(1, "WhitePlayer1", "BlackPlayer1", "Game1", game1));

        //simulate a non existent SQL db
        dropGameTable();

        assertThrows(DataAccessException.class, () -> gameDao.getAll(),"Method should throw SQL error");
    }






}
