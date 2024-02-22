package dataAccess;
import model.RegisterRequest;
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

    public boolean add(UserData user){
        return userData.add(user);
    }

    public UserData read(String username){
        for(UserData user : userData){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

}
