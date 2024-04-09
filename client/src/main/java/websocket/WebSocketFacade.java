package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import ui.GamePlayUserInterface;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.LeaveGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;

    boolean hasNewMessage = false;

    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {

        System.out.println("websocketfacade constructor");
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");

            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    notificationHandler.notify(message);
//                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    System.out.println("serverMessage");
//                    switch (serverMessage.getServerMessageType()) {
//                        case LOAD_GAME:
//                            System.out.println("LOAD_GAME");
//                            LoadGameMessage lgMessage = new Gson().fromJson(message, LoadGameMessage.class);
//                            loadGame(lgMessage);
//                            break;
//                        case NOTIFICATION:
//                            System.out.println("NOTIFICATION");
//                            break;
//                        case ERROR:
//                            System.out.println("ERROR");
//                            break;
//                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Session: " + session + "endPointConfig: " + endpointConfig);
    }

    public void joinChessGame(String authToken, ChessGame.TeamColor color, Integer gameID) throws ResponseException {
        try {
            System.out.println("joinChessGame");
            var command = new JoinPlayerCommand(authToken, color, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveChessGame(String authToken, Integer gameID) throws ResponseException {
        try {
            System.out.println("leaveChessGame");
            var command = new LeaveGameCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void loadGame(LoadGameMessage message){
        System.out.println("loadGame called");
        ChessBoard board = message.getGame().getBoard();
    }

    public boolean hasNewMessage(){
        if(hasNewMessage){
            hasNewMessage = false;
            return true;
        }
        else return false;
    }
}
