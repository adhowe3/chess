package dataAccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLUserDAO implements UserDAO{
    public MySQLUserDAO() throws DataAccessException{
        DatabaseManager.configureDatabase();
    }

    public void clearUserData() throws DataAccessException{
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "TRUNCATE TABLE userTable"
             )) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to clear userTable in database - " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException{
        return new UserData("usr", "ps", "email");
    }

    public boolean add(UserData user) throws DataAccessException{
        return true;
    }

    public ArrayList<UserData> getAll() throws DataAccessException{
        ArrayList<UserData> userDataArrayList = new ArrayList<>();
        return userDataArrayList;
    }



}
