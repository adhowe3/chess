package dataAccess;
import model.*;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface AuthDAO {
    void clearAuthData();
    void add(AuthData data) throws DataAccessException;
    AuthData getDataFromToken(String authToken);
    boolean delete(String authToken);
    ArrayList<AuthData> getAll();

}
