package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, boolean isObserver, boolean isWhite, Session session) {
        var connection = new Connection(authToken, isObserver, isWhite, session);
        connections.put(authToken, connection);
    }

    public boolean isObserver(String authToken){
        Connection c = connections.get(authToken);
        return c.isObserver;
    }

    public boolean isWhite(String authToken){
        Connection c = connections.get(authToken);
        return c.isWhite;
    }

    public void remove(String auth) {
        connections.remove(auth);
    }

    public void broadcast(String excludeJoinAuth, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeJoinAuth)) {
                    c.send(new Gson().toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }
}