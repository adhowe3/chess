package dataAccess;
import model.*;

import java.util.ArrayList;
import java.util.List;

public interface AuthDAO {
    void clearAuthData();

    public void add(AuthData data);

}
