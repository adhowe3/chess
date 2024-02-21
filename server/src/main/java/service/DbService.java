package service;
import dataAccess.*;

public class DbService {

    public Object clear(AuthDAO authDao, GameDAO gameDao, UserDAO userDao){
        authDao.clearAuthData();
        gameDao.clearGameData();
        userDao.clearUserData();

        // FIX ME Return an actual object with data
        return new Object();
    }
}
