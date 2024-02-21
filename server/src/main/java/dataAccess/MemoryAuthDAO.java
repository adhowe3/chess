package dataAccess;
import model.AuthData;
import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
    private ArrayList<AuthData> authData;

    public MemoryAuthDAO(){
        authData = new ArrayList<>();
    }

    public void add(AuthData data){
        authData.add(data);
    }

    public void delete(AuthData data){
        authData.remove(data);
    }

    public void clearAuthData(){
        authData.clear();
    }

}
