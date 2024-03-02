package dataAccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLAuthDAO implements AuthDAO{

    public MySQLAuthDAO() throws DataAccessException{
        DatabaseManager.configureDatabase();
    }

    public void clearAuthData() throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "TRUNCATE TABLE authTable"
             )) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to clear AuthData in database - " + e.getMessage());
        }
    }

    public void add(AuthData data) throws DataAccessException {
        if(getDataFromToken(data.getAuthToken()) != null){
            throw new DataAccessException("Error: authToken taken");
        }
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO authTable (username, authToken) VALUES (?, ?)"
             )) {
            preparedStatement.setString(1, data.getUsername());
            preparedStatement.setString(2, data.getAuthToken());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to add AuthData to database - " + e.getMessage());
        }
    }
    public AuthData getDataFromToken(String authToken) throws DataAccessException{
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT username, authToken FROM authTable WHERE authToken = ?"
            )){
            preparedStatement.setString(1, authToken);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedAuthToken = resultSet.getString("authToken");
                    String retrievedUsername = resultSet.getString("username");
                    return new AuthData(retrievedAuthToken, retrievedUsername);
                }
                else return null;
            }
        } catch(SQLException e){
            throw new DataAccessException("Error: Failed to get AuthData from database" + e.getMessage());
        }
    }

    public boolean delete(String authToken) throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM authTable WHERE authToken = ?"
             )) {
            preparedStatement.setString(1, authToken);
            int rowsAffected = preparedStatement.executeUpdate();
            // Check if any rows were deleted
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to delete AuthData from database - " + e.getMessage());
        }
    }

    public ArrayList<AuthData> getAll() throws DataAccessException{
        ArrayList<AuthData> authDataArrayList = new ArrayList<>();
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT username, authToken FROM authTable"
            )){
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    String retrievedAuthToken = resultSet.getString("authToken");
                    String retrievedUsername = resultSet.getString("username");
                    authDataArrayList.add(new AuthData(retrievedAuthToken, retrievedUsername));
                }
            }
        } catch(SQLException e){
            throw new DataAccessException("Error: Failed to get all AuthData from database" + e.getMessage());
        }
        return authDataArrayList;
    }

}
