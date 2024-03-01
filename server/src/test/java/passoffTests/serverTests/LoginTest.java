package passoffTests.serverTests;

import dataAccess.*;
import requests.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responses.LoginResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();

    @BeforeEach
    public void setDaos() throws Exception{
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
    }

    @Test
    public void loginSuccess() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);

        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        userDao.add(u);

        LoginRequest loginReq = new LoginRequest("Allan", "goodpassword");
        LoginResult lrs = service.login(loginReq);

        assertNotNull(authDao.getDataFromToken(lrs.authToken()));
        Assertions.assertEquals(u, userDao.getUser("Allan"), "userdata did not match expected");
    }

    @Test
    public void loginInWrongPassword() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);

        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        userDao.add(u);

        LoginRequest loginReq = new LoginRequest("Allan", "badpassword");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.login(loginReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage(), "Thrown error did not match expected error");
    }

    @Test
    public void loginInWrongUserName() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);

        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        userDao.add(u);

        LoginRequest loginReq = new LoginRequest("wrongName", "goodpassword");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.login(loginReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage(), "Thrown error did not match expected error");
    }

}
