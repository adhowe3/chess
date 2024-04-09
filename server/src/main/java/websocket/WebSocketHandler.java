package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.xml.sax.ErrorHandler;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.io.InvalidClassException;

@WebSocket
public class WebSocketHandler {

    private GameDAO gameDao;
    private AuthDAO authDao;
    private UserDAO userDao;
    private final ConnectionManager connections = new ConnectionManager();
    public WebSocketHandler(GameDAO gameDao, AuthDAO authDao, UserDAO userDao){
        System.out.println("WebSocketHandler constuctor");
        this.gameDao = gameDao;
        this.authDao = authDao;
        this.userDao = userDao;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER :
                System.out.println("JOIN_PLAYER");
                JoinPlayerCommand jpCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
                joinChessGame(jpCommand, session);
            break;
            case JOIN_OBSERVER:
                JoinObserverCommand obsCommand = new Gson().fromJson(message, JoinObserverCommand.class);
                observeChessGame(obsCommand, session);
            break;
            case MAKE_MOVE:
                MakeMoveCommand mkmCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeChessMove(mkmCommand, session);
            break;
            case LEAVE :
                LeaveGameCommand leaveCommand = new Gson().fromJson(message, LeaveGameCommand.class);
                leaveGame(leaveCommand, session);
            break;
            case RESIGN :
                ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                resignPlayer(resignCommand, session);
            break;
        }
    }

    public void leaveGame(LeaveGameCommand command, Session session) throws IOException{
        try{
            GameData gameData = gameDao.getGameDataFromID(command.getGameID());
            String name = authDao.getDataFromToken(command.getAuthString()).getUsername();

            if(!connections.isObserver(command.getAuthString())){
                if(connections.isWhite(command.getAuthString())){
                    gameData.setWhiteUsername(null);
                }
                else gameData.setBlackUsername(null);
            }
            gameDao.updateGame(gameData);
            NotificationMessage notificationMessage = new NotificationMessage(String.format("%s left the game", name));
            connections.broadcast("", command.getGameID(), notificationMessage);
            connections.remove(command.getAuthString());
        }catch(DataAccessException e){
            String errorMessage = new Gson().toJson(new ErrorMessage("error: " + e.getMessage()));
            session.getRemote().sendString(errorMessage);
        }
    }

    private void joinChessGame(JoinPlayerCommand command, Session session) throws IOException {
        String auth = command.getAuthString();
        NotificationMessage notificationMessage;
        if(command.getColor() == null) {
            observeChessGame(new JoinObserverCommand(command.getAuthString(), command.getGameID()), session);
            return;
        }
        boolean isWhite = (command.getColor().equals(ChessGame.TeamColor.WHITE));
        try{
            if(authDao.getDataFromToken(auth) != null) {
                String name = authDao.getDataFromToken(auth).getUsername();
                GameData gameData = gameDao.getGameDataFromID(command.getGameID());
                ServerMessage jsonMessage = messageMakerJoin(gameData, name, command);
                session.getRemote().sendString(new Gson().toJson(jsonMessage));

                // if it's not an error message, then notify all the other players that the person joined successfully
                if(!jsonMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
                    connections.add(auth, false, isWhite, command.getGameID(), session);
                    notificationMessage = new NotificationMessage(String.format("%s joining as %s", name, command.getColorStr()));
                    System.out.printf("%s joining as %s%n", name, command.getColorStr());
                    connections.broadcast(auth, command.getGameID(), notificationMessage);
                    String gameStr = new Gson().toJson(new LoadGameMessage(gameData.getGame()));
                    session.getRemote().sendString(gameStr);
                    System.out.println("gameID: " + gameData.getGameID() + "command gameID: "+ command.getGameID());
                }
            }
            else{
                String errorMessage = new Gson().toJson(new ErrorMessage("Error: bad authToken"));
                session.getRemote().sendString(errorMessage);
            }
        }
        catch(DataAccessException e){
            String errorMessage = new Gson().toJson(new ErrorMessage("error: " + e.getMessage()));
            session.getRemote().sendString(errorMessage);
        }
    }

    private void observeChessGame(JoinObserverCommand command, Session session) throws IOException {
        String auth = command.getAuthString();
        NotificationMessage notificationMessage;
        try{
            if(authDao.getDataFromToken(auth) != null) {
                String name = authDao.getDataFromToken(auth).getUsername();
                GameData gameData = gameDao.getGameDataFromID(command.getGameID());
                // if it's not an error message, then notify all the other players that the person joined successfully
                if(gameData != null){
                    connections.add(auth, true, false, command.getGameID(), session);
                    String loadGameMessage =new Gson().toJson(new LoadGameMessage(gameData.getGame()));
//                    connections.connections.get(auth).send(loadGameMessage);
                    session.getRemote().sendString(loadGameMessage);
                    notificationMessage = new NotificationMessage(String.format("%s is observing", name));
                    connections.broadcast(auth, command.getGameID(), notificationMessage);
                }
            }
            else{
                String errorMessage = new Gson().toJson(new ErrorMessage("Error: bad authToken"));
                session.getRemote().sendString(errorMessage);
            }
        }
        catch(DataAccessException e){
            String errorMessage = new Gson().toJson(new ErrorMessage("error: " + e.getMessage()));
            session.getRemote().sendString(errorMessage);
        }
    }

    private void makeChessMove(MakeMoveCommand command, Session session) throws IOException{
        ChessMove move = command.getMove();
        String auth = command.getAuthString();
        try{
            AuthData authData = authDao.getDataFromToken(auth);
            GameData gameData = gameDao.getGameDataFromID(command.getGameID());

            if(gameData.getGame().isGameIsOver()) throw new InvalidMoveException();
            if(!moveIsPlayersTurn(gameData, move, authData)) throw new InvalidMoveException();
            if(isGameOver(gameData)) throw new InvalidMoveException();

            gameData.getGame().makeMove(move);
            // update the database with the move
            gameDao.updateGame(gameData);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGame());
            connections.broadcast("", command.getGameID(), loadGameMessage);

            NotificationMessage notificationMessage = new NotificationMessage(String.format("%s moved: %s", authData.getUsername(), move));
            connections.broadcast(auth, command.getGameID(), notificationMessage);


        } catch(DataAccessException | InvalidMoveException e){
            String errorMessage = new Gson().toJson(new ErrorMessage("error: " + e.getMessage()));
            session.getRemote().sendString(errorMessage);
        }

    }

    private ServerMessage messageMakerJoin(GameData gameData, String name, JoinPlayerCommand command){
        if(command.getColor() == null)
        {
            return new ErrorMessage("Error: no team color");
        }
        else if(command.getColor().equals(ChessGame.TeamColor.BLACK)){
            if(gameData.getBlackUsername() == null || !gameData.getBlackUsername().equals(name)){
                return new ErrorMessage("Error: wrong team color");
            }
            else{
                return new LoadGameMessage(gameData.getGame());
            }
        }
        else if(command.getColor().equals(ChessGame.TeamColor.WHITE)){
            if(gameData.getWhiteUsername() == null || !gameData.getWhiteUsername().equals(name)){
                return new ErrorMessage("Error: wrong team color");
            }
            else{
                return new LoadGameMessage(gameData.getGame());
            }
        }
        else{
            return new ErrorMessage("Unknown Error");
        }
    }

    private void resignPlayer(ResignCommand command, Session session) throws IOException {
        try{
            GameData gameData = gameDao.getGameDataFromID(command.getGameID());
            AuthData authData = authDao.getDataFromToken(command.getAuthString());
            String name = authDao.getDataFromToken(command.getAuthString()).getUsername();
            if(connections.isObserver(authData.getAuthToken())) {
                String errorMessage = new Gson().toJson(new ErrorMessage("error: you are an observer"));
                session.getRemote().sendString(errorMessage);
                return;
            }
            if(gameData.getGame().isGameIsOver()){
                String errorMessage = new Gson().toJson(new ErrorMessage("error: game is already over"));
                session.getRemote().sendString(errorMessage);
                return;
            }
            gameData.getGame().setGameIsOver(true);
            gameDao.updateGame(gameData);
            NotificationMessage notificationMessage = new NotificationMessage(String.format("%s resigned from the game", name));
            connections.broadcast("", command.getGameID(), notificationMessage);
        }catch(DataAccessException e){
            String errorMessage = new Gson().toJson(new ErrorMessage("error: " + e.getMessage()));
            session.getRemote().sendString(errorMessage);
        }

    }

    private boolean isGameOver(GameData gameData){
        ChessGame game = gameData.getGame();
        ChessGame.TeamColor currColor = game.getTeamTurn();
        if(game.isInCheckmate(currColor) || game.isInCheckmate(game.getOppositeColor(currColor))) return true;
        return false;
    }

    private boolean moveIsPlayersTurn(GameData gameData, ChessMove move, AuthData authData){
        // return whether the piece that is being moved is the same color of the turn
        ChessGame.TeamColor currTeamColor = gameData.getGame().getTeamTurn();
        if(currTeamColor.equals(ChessGame.TeamColor.WHITE)){
            return authData.getUsername().equals(gameData.getWhiteUsername());
        }
        else if(currTeamColor.equals(ChessGame.TeamColor.BLACK)){
            return authData.getUsername().equals(gameData.getBlackUsername());
        }
        return false;
    }



}
