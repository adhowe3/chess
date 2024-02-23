package dataAccess;
import model.AuthData;
import model.RegisterRequest;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO{
    private ArrayList<UserData> userData;

    public MemoryUserDAO(){
        userData = new ArrayList<>();
    }
    @Override
    public void clearUserData(){
        userData.clear();
    }

    @Override
    public boolean add(UserData user){
        return userData.add(user);
    }

    @Override
    public UserData getUser(String username){
        for(UserData user : userData){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public String getPassword(String username){
        for(UserData user : userData){
            if(user.getUsername().equals(username)){
                return user.getPassword();
            }
        }
        return null;
    }

    public ArrayList<UserData> getAll(){
        return userData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryUserDAO that = (MemoryUserDAO) o;
        return Objects.equals(userData, that.userData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userData);
    }
}
