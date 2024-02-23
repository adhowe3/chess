package dataAccess;
import model.*;

import java.util.ArrayList;
import java.util.List;

public interface AuthDAO {
    void clearAuthData();

    public void add(AuthData data);

    public String getAuthToken(String username);

    public void delete(AuthData data);

    public ArrayList<AuthData> getAll();

}
