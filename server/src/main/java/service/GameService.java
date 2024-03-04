package service;
import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;

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
        if(gameReq.getGameName() == null){
            throw new DataAccessException("Error: bad request");
        }
        String username = authDao.getDataFromToken(gameReq.getAuthorization()).getUsername();
        GameData game = new GameData(newGameId, null, null, gameReq.getGameName(), new ChessGame());
        gameDao.add(game);
        return new CreateGameResponse(newGameId);
    }

    public void joinGame(JoinGameRequest joinReq) throws DataAccessException{
        if(authDao.getDataFromToken(joinReq.getAuthorization()) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        String usernameRequested = authDao.getDataFromToken(joinReq.getAuthorization()).getUsername();
        String colorRequested = joinReq.getPlayerColor();
        int idRequested = joinReq.getgameID();
        GameData gameRequested = gameDao.getGameDataFromID(joinReq.getgameID());
        if(gameRequested == null){
            throw new DataAccessException("Error: bad request");
        }
        if(colorRequested == null) return;
        if(colorRequested.equals("BLACK")){
            if(gameRequested.getBlackUsername() == null){
                gameDao.updateBlackUsername(idRequested, usernameRequested);
                return;
            }
            throw new DataAccessException("Error: already taken");
        }
        else if(colorRequested.equals("WHITE")){
            if(gameRequested.getWhiteUsername() == null){
                gameDao.updateWhiteUsername(idRequested, usernameRequested);
                return;
            }
            throw new DataAccessException("Error: already taken");
        }
        else{
            throw new DataAccessException("Error: already taken");
        }

    }

    public ListGamesResponse listGames(String authorization) throws DataAccessException{
        if(authDao.getDataFromToken(authorization) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        return new ListGamesResponse(gameDao.getAll());
    }

}
