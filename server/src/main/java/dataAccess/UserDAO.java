package dataAccess;

import model.AuthData;
import model.RegisterRequest;
import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    void clearUserData();

    UserData getUser(String username);

    boolean add(UserData user);

    String getPassword(String username);

    ArrayList<UserData> getAll();

}
