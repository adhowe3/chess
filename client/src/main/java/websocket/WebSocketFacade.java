package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import ui.GamePlayUserInterface;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.LeaveGameCommand;
import webSocketMessages.userCommands.MakeMoveCommand;
import webSocketMessages.userCommands.ResignCommand;

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
    public void resign(String authToken, Integer gameID) throws ResponseException{
        try {
            System.out.println("resign");
            var command = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, ChessMove move, Integer gameID) throws ResponseException{
        try {
            System.out.println("makeMove");
            var command = new MakeMoveCommand(authToken, move, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
