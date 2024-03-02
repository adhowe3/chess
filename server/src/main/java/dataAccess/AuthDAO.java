package dataAccess;
import model.*;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface AuthDAO {
    void clearAuthData() throws DataAccessException;
    void add(AuthData data) throws DataAccessException;
    AuthData getDataFromToken(String authToken) throws DataAccessException;
    boolean delete(String authToken) throws DataAccessException;
    ArrayList<AuthData> getAll();

}
