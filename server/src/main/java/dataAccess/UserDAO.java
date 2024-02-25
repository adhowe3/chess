package dataAccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    void clearUserData();

    UserData getUser(String username);

    boolean add(UserData user);

    ArrayList<UserData> getAll();

}
