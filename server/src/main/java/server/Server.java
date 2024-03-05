package server;
import handlers.Handler;
import spark.*;


public class Server {
    private Handler handler;
    public Server() {
        handler = new Handler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.init();

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> handler.clear(req, res));
        Spark.post("/user", (req, res) -> handler.register(req, res));
        Spark.post("/session", (req, res) -> handler.login(req, res));
        Spark.delete("/session", (req, res) -> handler.logout(req, res));
        Spark.post("/game", (req, res) -> handler.createGame(req,res));
        Spark.put("/game", (req, res) -> handler.joinGame(req,res));
        Spark.get("/game", (req, res) -> handler.listGames(req, res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}