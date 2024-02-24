package service;
import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResponse;
import model.GameData;

import java.util.concurrent.ThreadLocalRandom;

public class GameService {

    private GameDAO gameDao;
    private AuthDAO authDao;

    public GameService(GameDAO gameDao, AuthDAO authDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public CreateGameResponse createGame(CreateGameRequest gameReq) throws DataAccessException{
        if(authDao.getDataFromToken(gameReq.getAuthorization()) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if(gameDao.getGameData(gameReq.getGameName()) != null){
            throw new DataAccessException("Error: bad request");
        }
        String username = authDao.getDataFromToken(gameReq.getAuthorization()).getUsername();
        int newGameId = gameDao.nextGameID();
        GameData game = new GameData(newGameId, username, null, gameReq.getGameName(), new ChessGame());
        gameDao.add(game);
        return new CreateGameResponse(newGameId);
    }
}
