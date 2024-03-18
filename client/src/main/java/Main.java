import chess.*;
import com.google.gson.Gson;
import dataAccess.DatabaseManager;
import server.Server;
import ui.PreloginUI;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception{
//        Server server = new Server();
//        var port = server.run(8080);
//        System.out.println("Started test HTTP server on " + port);

        var serverUrl = "http://localhost:8080";
        PreloginUI loginUi = new PreloginUI(serverUrl);
        loginUi.getHelpCmd();
        loginUi.printPreloginUI();

    }
}