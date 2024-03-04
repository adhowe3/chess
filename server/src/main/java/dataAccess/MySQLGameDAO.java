package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class MySQLGameDAO implements GameDAO{
    public MySQLGameDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }
    @Override
    public void clearGameData() throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "TRUNCATE TABLE gameTable"
             )) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to clear gameTable in database - " + e.getMessage());
        }
    }
    @Override
    public void add(GameData data) throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO gameTable (whiteUsername, blackUsername, gameName,game) VALUES (?, ?, ?, ?)"
             )) {
            preparedStatement.setString(1, data.getWhiteUsername());
            preparedStatement.setString(2, data.getBlackUsername());
            preparedStatement.setString(3, data.getGameName());
            String gameJson = new Gson().toJson(data.getGame());
            preparedStatement.setString(4, gameJson);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to add gameData to database - " + e.getMessage());
        }
    }

    @Override
    public GameData getGameDataFromID(int gameID) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameID = ?"
             )) {
            preparedStatement.setInt(1, gameID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int retrievedGameID = resultSet.getInt("gameID");
                    String gameName = resultSet.getString("gameName");
                    String whiteUser = resultSet.getString("whiteUsername");
                    String blackUser = resultSet.getString("blackUsername");
                    String gameJson = resultSet.getString("game");
                    ChessGame chessGame = new Gson().fromJson(gameJson, ChessGame.class);
                    return new GameData(retrievedGameID, whiteUser, blackUser, gameName, chessGame);
                }
                else throw new DataAccessException("Error: bad request");
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error: cannot get gameData from gameID - " + e.getMessage());
        }
    }
    @Override
    public void updateWhiteUsername(int gameID, String username) throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE gameTable SET whiteUsername = ? WHERE gameID = ?"
             )) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: bad request");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update whiteUsername for gameID " + gameID + ": " + e.getMessage());
        }
    }
    @Override
    public void updateBlackUsername(int gameID, String username) throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE gameTable SET blackUsername = ? WHERE gameID = ?"
             )) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: bad request");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update blackUsername for gameID " + gameID + ": " + e.getMessage());
        }
    }
//    @Override
//    public int nextGameID(){
//        return 0;
//    }
    @Override
    public ArrayList<GameData> getAll(){
        return new ArrayList<>();
    }


}
