package service;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;
import java.util.UUID;

public class UserService {

    private UserDAO userDao;
    private AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao){
        this.userDao = userDao;
        this.authDao = authDao;
    }
    public RegisterResult register(RegisterRequest regReq) throws DataAccessException {
        // make sure the username is not taken, then add it to the Dao
        if(userDao.read(regReq.username()) == null){
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
}
