package dataAccess;
import model.*;

import java.util.ArrayList;
import java.util.List;

public interface AuthDAO {
    void clearAuthData();

    public void add(AuthData data);

    public String getAuthToken(String username);

    public String getAuthTokenFromToken(String authToken);

    public boolean delete(String authToken);
    public ArrayList<AuthData> getAll();

}
