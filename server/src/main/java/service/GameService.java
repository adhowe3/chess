package service;
import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResponse;
import model.GameData;
import model.JoinGameRequest;

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

    public void joinGame(JoinGameRequest joinReq) throws DataAccessException{
        if(authDao.getDataFromToken(joinReq.getAuthorization()) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        String usernameRequested = authDao.getDataFromToken(joinReq.getAuthorization()).getUsername();
        String colorRequested = joinReq.getPlayerColor();
        int idRequested = joinReq.getGameID();
        GameData gameRequested = gameDao.getGameDataFromID(joinReq.getGameID());
        if(colorRequested.isEmpty()){
            // join as observer
            return;
        }
        if(colorRequested.equals("BLACK")){
            if(gameRequested.getBlackUsername().isBlank()){
                gameDao.updateBlackUsername(idRequested, usernameRequested);
                return;
            }
            throw new DataAccessException("Error: already taken");
        }
        if(colorRequested.equals("WHITE")){
            if(gameRequested.getWhiteUsername().isBlank()){
                gameDao.updateWhiteUsername(idRequested, usernameRequested);
                return;
            }
            throw new DataAccessException("Error: already taken");
        }
        else{
            throw new DataAccessException("Error: bad request");
        }

    }

}
