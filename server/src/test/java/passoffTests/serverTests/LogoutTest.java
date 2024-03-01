package passoffTests.serverTests;

import dataAccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LogoutRequest;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTest {
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();

    UserData u;
    AuthData a;
    UserService service;

    @BeforeEach
    public void setDaos() throws Exception{
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

        assertNotNull(authDao.getDataFromToken(a.getAuthToken()));

        LogoutRequest logoutReq = new LogoutRequest(a.getAuthToken());
        service.logout(logoutReq);

        Assertions.assertNull(authDao.getDataFromToken(a.getAuthToken()));
        Assertions.assertEquals(u, userDao.getUser("Allan"), "user data did not match expected value");
    }

    @Test
    public void logoutWrongAuth() throws DataAccessException{
        LogoutRequest logoutReq = new LogoutRequest("wrongAuthToken");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.logout(logoutReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage(), "Thrown error did not match expected error");
    }


}
