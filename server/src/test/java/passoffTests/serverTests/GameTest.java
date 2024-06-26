package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import service.GameService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTest {

    private GameService service;
    private GameDAO gameDao;
    private AuthDAO authDao;

    private CreateGameRequest req;
    GameData g;
    AuthData a;


    @BeforeEach
    public void setup() throws Exception{
        gameDao = new MemoryGameDAO();
        authDao = new MemoryAuthDAO();
        req = new CreateGameRequest("testAuthToken", "gameName");
        a = new AuthData("testAuthToken", "Allan");
        authDao.add(a);
        g = new GameData(1234, "whiteuser", "blackuser", "FirstGame", new ChessGame());
        gameDao.add(g);
        service = new GameService(gameDao, authDao);
    }

    @Test
    public void CreateGameSuccess() throws DataAccessException {
        CreateGameResponse res = service.createGame(req);
        Assertions.assertNotNull(res, "CreateGameResponse record object should not be null");
        Assertions.assertTrue(res.gameID() > 0, "The game ID should be a positive integer");
    }

    @Test
    public void CreateGameBadAuth() throws DataAccessException {
        CreateGameResponse res = service.createGame(req);
        req.setAuthorization(req.getAuthorization() + "giberish");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.createGame(req));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage(), "allowed unauthorized token to create a game");
    }

    @Test
    public void JoinGameSuccess() throws DataAccessException{
        GameData gm = new GameData(123, null, null, "SecondGame", new ChessGame());
        gameDao.add(gm);
        AuthData au = new AuthData("testAuth", "Sam");
        authDao.add(au);
        JoinGameRequest joinReq = new JoinGameRequest(au.getAuthToken(),"WHITE", 123, 1);
        service.joinGame(joinReq);
        GameData compGm = new GameData(123, au.getUsername(), null, "SecondGame", new ChessGame());
        Assertions.assertEquals(compGm, gameDao.getGameDataFromID(123), "Joining the game did not match expected database");
    }

    @Test
    public void JoinGameWhiteTaken() throws DataAccessException{
        GameData gm = new GameData(123, "John", "", "SecondGame", new ChessGame());
        gameDao.add(gm);
        AuthData au = new AuthData("testAuth", "Sam");
        authDao.add(au);
        JoinGameRequest joinReq = new JoinGameRequest(au.getAuthToken(), "WHITE", 123, 1);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.joinGame(joinReq));
        Assertions.assertEquals("Error: already taken", exception.getMessage(), "Allowed user to take an already taken color");
    }

    @Test
    public void ListGamesSuccess() throws DataAccessException{
        GameData g1 = new GameData(1, "allan", "sam", "FirstGamme1", new ChessGame());
        gameDao.add(g1);
        GameData g2 = new GameData(2, "vincent", "Bryce", "really not first", new ChessGame());
        gameDao.add(g2);

        ListGamesResponse lsr = service.listGames(a.getAuthToken());

        ArrayList<GameData> compareList = new ArrayList<>();
        compareList.add(g);
        compareList.add(g1);
        compareList.add(g2);
        Assertions.assertEquals(compareList, lsr.games(), "list did not match expected response");
    }

    @Test
    public void ListGamesUnauthorized() throws DataAccessException{
        GameData g1 = new GameData(1, "allan", "sam", "FirstGamme1", new ChessGame());
        gameDao.add(g1);
        GameData g2 = new GameData(2, "vincent", "Bryce", "really not first", new ChessGame());
        gameDao.add(g2);
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class, () -> service.listGames(a.getAuthToken() + "bad auth"));
        Assertions.assertEquals("Error: unauthorized", ex.getMessage(), "expected a DataAccessException throw");
    }

}
