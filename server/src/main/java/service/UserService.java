package service;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.*;

import java.util.UUID;

public class UserService {

    private UserDAO userDao;
    private AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao){
        this.userDao = userDao;
        this.authDao = authDao;
    }
    public RegisterResult register(RegisterRequest regReq) throws DataAccessException {

        if(regReq.username() == null || regReq.password() == null || regReq.email() == null){
            throw new DataAccessException("Error: bad request");
        }

        // make sure the username is not taken, then add it to the Dao
        if(userDao.getUser(regReq.username()) == null){
            UserData newUser = new UserData(regReq.username(), regReq.password(), regReq.email());
            userDao.add(newUser);
            // make new authToken associated with this username, add to authDao
            String authToken = UUID.randomUUID().toString();
            authDao.add(new AuthData(authToken, regReq.username()));
            return new RegisterResult(regReq.username(), authToken);
        }
        else{
            throw new DataAccessException("Error: already taken");
        }
    }

    public LoginResult login(LoginRequest loginReq) throws DataAccessException{
        if(userDao.getUser(loginReq.username()) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if(!userDao.getUser(loginReq.username()).getPassword().equals(loginReq.password())){
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = UUID.randomUUID().toString();
        authDao.add(new AuthData(authToken, loginReq.username()));
        return new LoginResult(loginReq.username(), authToken);
    }

    public void logout(LogoutRequest logoutReq) throws DataAccessException{
        if(logoutReq.authorization() == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if(!authDao.delete(logoutReq.authorization())){
            throw new DataAccessException("Error: unauthorized");
        }
    }

}
