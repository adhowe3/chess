package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;

    public boolean isObserver;

    public Connection(String authToken, boolean isObserver, Session session) {
        this.authToken = authToken;
        this.session = session;
        this.isObserver = isObserver;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}