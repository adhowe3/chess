package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.GamePlayUserInterface;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    GamePlayUserInterface gamePlayUserInterface;

    public WebSocketFacade(String url) throws ResponseException {
        System.out.println("websocketfacade constructor");
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                System.out.println(serverMessage.getServerMessageType());
                switch(serverMessage.getServerMessageType()){
                    case LOAD_GAME :
                        LoadGameMessage lgMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        loadGame(lgMessage);
                        System.out.println("LOAD_GAME");
                        break;
                    case NOTIFICATION:
                        System.out.println(serverMessage);
                        break;
                    case ERROR:
                        break;
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinChessGame(String authToken, ChessGame.TeamColor color, Integer gameID) throws ResponseException {
        try {
            System.out.println("joinChessGame");
            var command = new JoinPlayerCommand(authToken, color, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.gamePlayUserInterface = new GamePlayUserInterface(command);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void loadGame(LoadGameMessage message){
        System.out.println("loadGame called");
        ChessBoard board = message.getGame().getBoard();
        gamePlayUserInterface.printChessBoard(board);
    }
}
