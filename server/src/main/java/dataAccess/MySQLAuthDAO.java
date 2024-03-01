package dataAccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLAuthDAO implements AuthDAO{

    public MySQLAuthDAO() throws DataAccessException{
        DatabaseManager.configureDatabase();
    }

    public void clearAuthData(){

    }

    public void add(AuthData data) throws DataAccessException {
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
    public AuthData getDataFromToken(String authToken){
        AuthData data = new AuthData(authToken,"temp");

        return data;
    }

    public boolean delete(String authToken){
        return true;
    }
    public ArrayList<AuthData> getAll(){
        ArrayList<AuthData> authDataArrayList = new ArrayList<>();

        return authDataArrayList;
    }
}
