package passoffTests.serverTests;

import dataAccess.*;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginTest {
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();

    @BeforeEach
    public void setDaos(){
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
    }

    @Test
    public void loginSuccess() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);

        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        userDao.add(u);

        LoginRequest loginReq = new LoginRequest("Allan", "goodpassword");
        service.login(loginReq);

        assertFalse(authDao.getAuthToken("Allan").isEmpty());
        Assertions.assertEquals(userDao.getUser("Allan"),  u);

    }

    @Test
    public void loginInWrongPassword() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);

        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        userDao.add(u);

        LoginRequest loginReq = new LoginRequest("Allan", "badpassword");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.login(loginReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void loginInWrongUserName() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);

        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        userDao.add(u);

        LoginRequest loginReq = new LoginRequest("wrongName", "goodpassword");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.login(loginReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

}
