package passoffTests.serverTests;

import dataAccess.*;
import requests.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest {
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();

    @BeforeEach
    public void setDaos(){
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
    }

    @Test
    public void registerSuccess() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);
        RegisterRequest regReq = new RegisterRequest("Allan", "goodpassword", "myemail@ymail.com");
        service.register(regReq);

        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");

        Assertions.assertFalse(authDao.getDataFromUser("Allan").getAuthToken().isEmpty());
        Assertions.assertEquals(u, userDao.getUser("Allan"), "user data did not match expected userData");

    }

    @Test
    public void registerDuplicateUserName() throws DataAccessException{
        UserService service = new UserService(userDao, authDao);
        RegisterRequest regReq = new RegisterRequest("Allan", "password", "myemail@ymail.com");
        RegisterRequest regReq2 = new RegisterRequest("Allan", "anotherPassword", "myemail3@ymail.com");

        // Register the first user successfully
        service.register(regReq);
        // Use assertThrows to assert that the second registration attempt throws a DataAccessException
        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.register(regReq2));
        // Assert the message of the exception
        Assertions.assertEquals("Error: already taken", exception.getMessage(), "Thrown exception did not match expected");
    }

}
