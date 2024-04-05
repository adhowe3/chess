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
                notificationMessage = new NotificationMessage(String.format("%s joining as %s", name, command.getColorStr()));
                System.out.println(String.format("%s joining as %s", name, command.getColorStr()));
                connections.broadcast(auth, notificationMessage);
            }
            GameData gameData = gameDao.getGameDataFromID(command.getGameID());
            if(gameData != null){
               LoadGameMessage game = new LoadGameMessage(gameData);
               String gameStr = new Gson().toJson(game);
               System.out.println(gameStr);
               session.getRemote().sendString(new Gson().toJson(game));
            }
        }
        catch(DataAccessException e){
            notificationMessage = new NotificationMessage("error: " + e.getMessage());
            connections.broadcast(auth, notificationMessage);
        }
    }

}
