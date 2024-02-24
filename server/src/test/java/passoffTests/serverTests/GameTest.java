package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

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
    public void setup(){
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
    public void CreateDuplicateGame() throws DataAccessException {
        CreateGameResponse res = service.createGame(req);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> service.createGame(req));
        Assertions.assertEquals("Error: bad request", exception.getMessage());
    }

    @Test
    public void JoinGameSuccess() throws DataAccessException{
        GameData gm = new GameData(123, "", "", "SecondGame", new ChessGame());
        gameDao.add(gm);
        AuthData au = new AuthData("testAuth", "Sam");
        authDao.add(au);
        JoinGameRequest joinReq = new JoinGameRequest("WHITE", 123, au.getAuthToken());
        service.joinGame(joinReq);
        GameData compGm = new GameData(123, au.getUsername(), "", "SecondGame", new ChessGame());
        Assertions.assertEquals(gameDao.getGameData("SecondGame"), compGm);
    }

    @Test
    public void JoinGameWhiteTaken() throws DataAccessException{
        GameData gm = new GameData(123, "John", "", "SecondGame", new ChessGame());
        gameDao.add(gm);
        AuthData au = new AuthData("testAuth", "Sam");
        authDao.add(au);
        JoinGameRequest joinReq = new JoinGameRequest("WHITE", 123, au.getAuthToken());
        service.joinGame(joinReq);
        GameData compGm = new GameData(123, gm.getWhiteUsername(), "", "SecondGame", new ChessGame());
        Assertions.assertEquals(gameDao.getGameData("SecondGame"), compGm);
    }


}
