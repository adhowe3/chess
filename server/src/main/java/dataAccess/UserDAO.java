package dataAccess;

import model.RegisterRequest;
import model.UserData;

public interface UserDAO {
    void clearUserData();

    UserData read(String username);

    boolean add(UserData user);

}
