package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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
    public WebSocketHandler(){
        try{
            this.gameDao = new MySQLGameDAO();
            this.authDao = new MySQLAuthDAO();
            this.userDao = new MySQLUserDAO();
        }
        catch(DataAccessException e){
            System.out.println("Failed to initialize the database");
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER :
                JoinPlayerCommand jpCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
                joinChessGame(jpCommand, session);
            break;
//            case JOIN_OBSERVER -> exit(action.visitorName());
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
            if(authDao.getDataFromToken(auth) == null) {
                String name = authDao.getDataFromToken(auth).getUsername();
                notificationMessage = new NotificationMessage(String.format("%s joining as %s", name, command.getColorStr()));
                connections.broadcast(auth, notificationMessage);
            }
            GameData gameData = gameDao.getGameDataFromID(command.getGameID());
            if(gameData != null){
               String game = new Gson().toJson(new LoadGameMessage(gameData.getGame()));
               session.getRemote().sendString(game);
            }
        }
        catch(DataAccessException e){
            notificationMessage = new NotificationMessage("error: " + e.getMessage());
            connections.broadcast(auth, notificationMessage);
        }
    }

}
