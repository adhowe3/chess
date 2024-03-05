package dataAccess;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT username, password, email FROM userTable WHERE username = ?"
            )){
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedUsername = resultSet.getString("username");
                    String retrievedPassword = resultSet.getString("password");
                    String retrievedEmail = resultSet.getString("email");
                    return new UserData(retrievedUsername, retrievedPassword, retrievedEmail);
                }
                else return null;
            }
        } catch(SQLException e){
            throw new DataAccessException("Error: Failed to get UserData from database" + e.getMessage());
        }
    }

    public boolean add(UserData user) throws DataAccessException{
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if(getUser(user.getUsername()) != null){
            throw new DataAccessException("Error: already taken");
        }
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO userTable (username,password,email) VALUES (?, ?, ?)"
             )) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, getHashedPassword(user.getPassword()));
            preparedStatement.setString(3, user.getEmail());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to add userData to database - " + e.getMessage());
        }
        return true;
    }

    public ArrayList<UserData> getAll() throws DataAccessException{
        ArrayList<UserData> userDataArrayList = new ArrayList<>();
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT username, password, email FROM userTable"
            )){
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String retrievedUsername = resultSet.getString("username");
                    String retrievedPassword = resultSet.getString("password");
                    String retrievedEmail = resultSet.getString("email");
                    userDataArrayList.add(new UserData(retrievedUsername, retrievedPassword, retrievedEmail));
                }
            }
        } catch(SQLException e){
            throw new DataAccessException("Error: Failed to get UserData from database" + e.getMessage());
        }
        return userDataArrayList;
    }

    String getHashedPassword(String textPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(textPassword);
    }





}
