package dataAccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    void clearUserData() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    boolean add(UserData user) throws DataAccessException;

    ArrayList<UserData> getAll() throws DataAccessException;

}
