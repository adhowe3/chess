package dataAccess;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO{
    private ArrayList<AuthData> authData;

    public MemoryAuthDAO(){
        authData = new ArrayList<>();
    }

    @Override
    public void add(AuthData data){
        authData.add(data);
    }

    @Override
    public boolean delete(String authToken){
        for(AuthData data : authData){
            if(data.getAuthToken().equals(authToken))
                return authData.remove(data);
        }
        return false;
    }

    @Override
    public AuthData getDataFromToken(String authToken){
        for(AuthData data : authData){
            if(data.getAuthToken().equals(authToken))
                return data;
        }
        return null;
    }

    @Override
    public void clearAuthData(){
        authData.clear();
    }

    public ArrayList<AuthData> getAll(){
        return authData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(authData, that.authData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authData);
    }
}
