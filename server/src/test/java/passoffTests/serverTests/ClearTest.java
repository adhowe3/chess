package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import service.DbService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearTest {

    private GameDAO gameDao = new MemoryGameDAO();
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();

    @BeforeEach
    public void setDb() throws Exception{
        gameDao = new MemoryGameDAO();
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();

        GameData g = new GameData(1, "Allan", "Kalon", "Cool Game", new ChessGame());
        gameDao.add(g);
        AuthData a = new AuthData("Test Auth Token", "Test Name");
        authDao.add(a);
        UserData u = new UserData("Test Username", "Test Password", "Test Email" );
        userDao.add(u);
    }

    @Test
    public void clearSuccess(){
        assertFalse(authDao.getAll().isEmpty());
        assertFalse(gameDao.getAll().isEmpty());
        assertFalse(userDao.getAll().isEmpty());

        DbService service = new DbService(authDao, gameDao, userDao);
        service.clear();
        // the Daos should now be empty like a new one
        Assertions.assertEquals(authDao, new MemoryAuthDAO());
        Assertions.assertEquals(gameDao, new MemoryGameDAO());
        Assertions.assertEquals(userDao, new MemoryUserDAO());
    }
}
