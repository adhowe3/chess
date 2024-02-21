package dataAccess;
import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{
    private ArrayList<UserData> userData;
    public MemoryUserDAO(){
        userData = new ArrayList<>();
    }

    public void clearUserData(){
        userData.clear();
    }

}
