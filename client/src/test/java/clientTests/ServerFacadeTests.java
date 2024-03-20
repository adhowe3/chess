package clientTests;

import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import responses.RegisterResult;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static AuthDAO authDao;
    private static UserDAO userDao;
    private static GameDAO gameDao;
    private String serverUrl = "http://localhost:8080";
    private ServerFacade serverFacade = new ServerFacade(serverUrl);

    public ServerFacadeTests() throws DataAccessException {
        authDao = new MySQLAuthDAO();
        userDao = new MySQLUserDAO();
        gameDao = new MySQLGameDAO();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDao.clearAuthData();
        userDao.clearUserData();
        gameDao.clearGameData();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testRegisterPositive() throws ResponseException, DataAccessException {
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");

        AuthData resResult = serverFacade.registerUser(u);

        Assertions.assertNotNull(authDao.getDataFromToken(resResult.getAuthToken()));
        Assertions.assertEquals(u.getUsername(), userDao.getUser("Allan").getUsername(), "username data did not match expected username");
        Assertions.assertEquals(u.getEmail(), userDao.getUser("Allan").getEmail(), "email data did not match expected email");
    }

    @Test
    public void testRegisterNegative() throws ResponseException, DataAccessException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");

        AuthData resResult = serverFacade.registerUser(u);

        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.registerUser(u));
        // Assert the message of the exception
        Assertions.assertEquals("failure: 403", exception.getMessage(), "Thrown exception did not match expected");

    }

}
