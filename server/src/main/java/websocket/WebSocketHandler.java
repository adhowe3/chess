package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.xml.sax.ErrorHandler;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private GameDAO gameDao;
    private AuthDAO authDao;
    private UserDAO userDao;
    private final ConnectionManager connections = new ConnectionManager();
    public WebSocketHandler(GameDAO gameDao, AuthDAO authDao, UserDAO userDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
        this.userDao = userDao;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER :
                JoinPlayerCommand jpCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
                joinChessGame(jpCommand, session);
            break;
            case JOIN_OBSERVER:
                 System.out.println("join oserver websocket handler");
//               exit(action.visitorName());
            break;
//            case MAKE_MOVE -> ;
//            case LEAVE -> ;
//            case RESIGN -> ;
        }
    }

    private void joinChessGame(JoinPlayerCommand command, Session session) throws IOException {
        String auth = command.getAuthString();
        NotificationMessage notificationMessage;
        connections.add(auth, session);
        try{
            if(authDao.getDataFromToken(auth) != null) {
                String name = authDao.getDataFromToken(auth).getUsername();
                GameData gameData = gameDao.getGameDataFromID(command.getGameID());
                ServerMessage jsonMessage = messageMaker(gameData, name, command);
                session.getRemote().sendString(new Gson().toJson(jsonMessage));

                // if it's not an error message, then notify all the other players that the person joined successfully
                if(!jsonMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
                    notificationMessage = new NotificationMessage(String.format("%s joining as %s", name, command.getColorStr()));
                    connections.broadcast(auth, notificationMessage);
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

    private ServerMessage messageMaker(GameData gameData, String name, JoinPlayerCommand command){
        if(command.getColor() == null)
        {
            return new ErrorMessage("Error: no team color");
        }
        else if(command.getColor().equals(ChessGame.TeamColor.BLACK)){
            if(gameData.getBlackUsername() == null || !gameData.getBlackUsername().equals(name)){
                return new ErrorMessage("Error: wrong team color");
            }
            else{
                return new LoadGameMessage(gameData);
            }
        }
        else if(command.getColor().equals(ChessGame.TeamColor.WHITE)){
            if(gameData.getWhiteUsername() == null || !gameData.getWhiteUsername().equals(name)){
                return new ErrorMessage("Error: wrong team color");
            }
            else{
                return new LoadGameMessage(gameData);
            }
        }
        else{
            return new ErrorMessage("Unknown Error");
        }
    }

}
