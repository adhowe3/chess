package clientTests;

import chess.ChessGame;
import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import responses.LoginResult;
import responses.RegisterResult;
import server.Server;
import server.ServerFacade;
import service.UserService;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static AuthDAO authDao;
    private static UserDAO userDao;
    private static GameDAO gameDao;
    private static String serverUrl;
    private static ServerFacade serverFacade;

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

        serverUrl = "http://localhost:" + port;
        serverFacade = new ServerFacade(serverUrl);
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

    @Test
    public void loginSuccess() throws DataAccessException, ResponseException {
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        LoginRequest loginReq = new LoginRequest(u.getUsername(), u.getPassword());
        serverFacade.registerUser(u);
        AuthData loginResult = serverFacade.loginUser(loginReq);

        Assertions.assertNotNull(authDao.getDataFromToken(loginResult.getAuthToken()));
    }

    @Test
    public void loginBadpassword() throws DataAccessException, ResponseException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        LoginRequest loginReq = new LoginRequest(u.getUsername(), "badpassword");
        serverFacade.registerUser(u);
        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.loginUser(loginReq));
        // Assert the message of the exception
        Assertions.assertEquals("failure: 401", exception.getMessage(), "Thrown exception did not match expected");
    }

    @Test
    public void logoutSuccess() throws DataAccessException, ResponseException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        LoginRequest loginReq = new LoginRequest(u.getUsername(), u.getPassword());

        serverFacade.registerUser(u);
        AuthData loginResult = serverFacade.loginUser(loginReq);
        Assertions.assertNotNull(authDao.getDataFromToken(loginResult.getAuthToken()));

        serverFacade.logoutUser(loginResult.getAuthToken());
        Assertions.assertNull(authDao.getDataFromToken(loginResult.getAuthToken()), "should be removed from authDao");
    }

    @Test
    public void logoutBadAuth() throws DataAccessException, ResponseException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        LoginRequest loginReq = new LoginRequest(u.getUsername(), u.getPassword());
        serverFacade.registerUser(u);
        AuthData loginResult = serverFacade.loginUser(loginReq);

        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.logoutUser("bad auth token"));
        // Assert the message of the exception
        Assertions.assertEquals("failure: 401", exception.getMessage(), "Thrown exception did not match expected");
    }

    @Test
    public void createGameSuccess() throws ResponseException, DataAccessException {
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        AuthData resResult = serverFacade.registerUser(u);
        CreateGameRequest gameReq = new CreateGameRequest(resResult.getAuthToken(), "gameName");
        CreateGameResponse gameRes = serverFacade.createGame(gameReq);

        Assertions.assertNotNull(gameDao.getGameDataFromID(gameRes.gameID()) );
    }

    @Test
    public void createGameBadAuth() throws ResponseException, DataAccessException {
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        AuthData resResult = serverFacade.registerUser(u);
        CreateGameRequest gameReq = new CreateGameRequest("bad auth", "gameName");

        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.createGame(gameReq));
        // Assert the message of the exception
        Assertions.assertEquals("failure: 401", exception.getMessage(), "Thrown exception did not match expected");
    }

    @Test
    public void listGamesSuccess() throws ResponseException, DataAccessException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        AuthData resResult = serverFacade.registerUser(u);
        ListGamesResponse listRes = serverFacade.listGames(resResult.getAuthToken());
        Assertions.assertTrue(listRes.games().isEmpty());

        CreateGameRequest gameReq = new CreateGameRequest(resResult.getAuthToken(), "gameName");
        CreateGameResponse gameRes = serverFacade.createGame(gameReq);

        listRes = serverFacade.listGames(resResult.getAuthToken());
        Assertions.assertFalse(listRes.games().isEmpty());

        Assertions.assertNotNull(gameDao.getGameDataFromID(gameRes.gameID()) );
    }

    @Test
    public void listGamesBadAuth() throws ResponseException, DataAccessException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        AuthData resResult = serverFacade.registerUser(u);
        CreateGameRequest gameReq = new CreateGameRequest(resResult.getAuthToken(), "gameName");
        serverFacade.createGame(gameReq);

        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.listGames("bad auth"));
        // Assert the message of the exception
        Assertions.assertEquals("failure: 401", exception.getMessage(), "Thrown exception did not match expected");
    }

    @Test
    public void joinGameSuccess() throws ResponseException, DataAccessException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        AuthData resResult = serverFacade.registerUser(u);
        CreateGameRequest gameReq = new CreateGameRequest(resResult.getAuthToken(), "gameName");
        CreateGameResponse gameRes = serverFacade.createGame(gameReq);
        JoinGameRequest joinReq = new JoinGameRequest(resResult.getAuthToken(), "WHITE", gameRes.gameID(), 1);
        serverFacade.joinGame(joinReq);

        Assertions.assertEquals(joinReq.getgameID(),  gameDao.getAll().getFirst().getGameID());
        Assertions.assertEquals(u.getUsername(),  gameDao.getAll().getFirst().getWhiteUsername());

        Assertions.assertNotNull(gameDao.getGameDataFromID(gameRes.gameID()));
    }

    @Test
    public void joinGameBadColor() throws ResponseException, DataAccessException{
        UserData u = new UserData("Allan", "goodpassword", "myemail@ymail.com");
        AuthData resResult = serverFacade.registerUser(u);
        CreateGameRequest gameReq = new CreateGameRequest(resResult.getAuthToken(), "gameName");
        CreateGameResponse gameRes = serverFacade.createGame(gameReq);
        String[] userInput = {"join", "1", "badcolor"};
        JoinGameRequest joinReq = new JoinGameRequest(resResult.getAuthToken(), "bad color", gameRes.gameID(), 1);
        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.joinGame(joinReq));
        // Assert the message of the exception
        Assertions.assertEquals("failure: 400", exception.getMessage(), "Thrown exception did not match expected");
    }



}
