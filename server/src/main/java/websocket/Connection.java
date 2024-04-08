package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;

    public boolean isObserver;
    public boolean isWhite;

    public Connection(String authToken, boolean isObserver, boolean isWhite, Session session) {
        this.authToken = authToken;
        this.session = session;
        this.isObserver = isObserver;
        this.isWhite = isWhite;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}