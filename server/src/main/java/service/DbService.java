package service;
import dataAccess.*;

public class DbService {

    private AuthDAO authDao;
    private GameDAO gameDao;
    private UserDAO userDao;

    public DbService(AuthDAO authDao, GameDAO gameDao, UserDAO userDao){
        this.authDao = authDao;
        this.gameDao = gameDao;
        this.userDao = userDao;
    }
    public void clear(){
        authDao.clearAuthData();
        gameDao.clearGameData();
        userDao.clearUserData();
    }

}
