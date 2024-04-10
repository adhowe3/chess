package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    public void updateGame(GameData data) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE gameTable SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?"
             )) {
            String gameJson = new Gson().toJson(data.getGame());
            preparedStatement.setString(1, data.getWhiteUsername());
            preparedStatement.setString(2, data.getBlackUsername());
            preparedStatement.setString(3, gameJson);
            preparedStatement.setInt(4, data.getGameID());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: No rows updated. GameID " + data.getGameID() + " not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to update gameData in database - " + e.getMessage());
        }
    }

    @Override
    public void updateWhiteUsername(int gameID, String username) throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE gameTable SET whiteUsername = ? WHERE gameID = ?"
             )) {
            updateName(preparedStatement, gameID, username);
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to update whiteUsername for gameID " + gameID + ": " + e.getMessage());
        }
    }
    @Override
    public void updateBlackUsername(int gameID, String username) throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE gameTable SET blackUsername = ? WHERE gameID = ?"
             )) {
            updateName(preparedStatement, gameID, username);
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to update blackUsername for gameID " + gameID + ": " + e.getMessage());
        }
    }

    private void updateName(PreparedStatement preparedStatement, int gameID, String username) throws DataAccessException{
        try{
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: bad request");
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error: Failed to update blackUsername for gameID " + gameID + ": " + e.getMessage());
        }
    }

    @Override
    public int nextGameID() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT COUNT(*) AS total FROM gameTable"
             )) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int totalRows = resultSet.getInt("total");
                    return totalRows + 1;
                } else {
                    return 1;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to get nextGameID: " + e.getMessage());
        }
    }
    @Override
    public ArrayList<GameData> getAll() throws DataAccessException{
        ArrayList<GameData> gameData = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable"
             )) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int retrievedGameID = resultSet.getInt("gameID");
                    String retrievedBlackUser = resultSet.getString("blackUsername");
                    String retrievedWhiteUser = resultSet.getString("whiteUsername");
                    String retrievedGameName = resultSet.getString("gameName");
                    String retrievedGameJson = resultSet.getString("game");
                    ChessGame chessGame = new Gson().fromJson(retrievedGameJson, ChessGame.class);
                    gameData.add(new GameData(retrievedGameID, retrievedWhiteUser, retrievedBlackUser, retrievedGameName, chessGame));
                }
                return gameData;
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error: cannot get gameData from gameID - " + e.getMessage());
        }
    }


}
