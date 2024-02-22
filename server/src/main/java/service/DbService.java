package service;
import dataAccess.*;

public class DbService {

    public void clear(AuthDAO authDao, GameDAO gameDao, UserDAO userDao){
        authDao.clearAuthData();
        gameDao.clearGameData();
        userDao.clearUserData();
    }

}
