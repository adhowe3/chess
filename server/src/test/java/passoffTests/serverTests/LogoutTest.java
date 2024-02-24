package passoffTests.serverTests;

import dataAccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTest {
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();

    UserData u;
    AuthData a;
    UserService service;

    @BeforeEach
    public void setDaos(){
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
        u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        userDao.add(u);
        a = new AuthData("testAuthToken", "Allan");
        authDao.add(a);
        service = new UserService(userDao, authDao);
    }

    @Test
    public void logoutSuccess() throws DataAccessException{

        Assertions.assertFalse(authDao.getDataFromUser("Allan").getAuthToken().isEmpty());

        LogoutRequest logoutReq = new LogoutRequest("testAuthToken");
        service.logout(logoutReq);

        Assertions.assertNull(authDao.getDataFromUser("Allan"));
        Assertions.assertEquals(userDao.getUser("Allan"),  u);

    }

    @Test
    public void logoutWrongAuth() throws DataAccessException{
        LogoutRequest logoutReq = new LogoutRequest("wrongAuthToken");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.logout(logoutReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }


}
