package handlers;
import com.google.gson.Gson;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.*;
import service.GameService;
import service.UserService;
import spark.Response;
import spark.Request;
import service.DbService;
import dataAccess.*;

import javax.xml.crypto.Data;

public class Handler {
    private GameDAO gameDao = new MemoryGameDAO();
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();
    Gson serializer = new Gson();
    public Handler(){}

    public Object clear(Request req, Response res){
        DbService service = new DbService(authDao, gameDao, userDao);
        service.clear();
        return "{}";
    }

    public Object register(Request req, Response res) {
        UserService service = new UserService(userDao, authDao);
        RegisterRequest objReq = serializer.fromJson(req.body(), RegisterRequest.class);

        try{
            RegisterResult objRes = service.register(objReq);
            res.body(serializer.toJson(objRes));
        }
        catch(DataAccessException e){
            setResponse(e, res);
        }
        return res.body();
    }

    public Object login(Request req, Response res){
        UserService service = new UserService(userDao, authDao);
        LoginRequest objReq = serializer.fromJson(req.body(), LoginRequest.class);
        try{
            LoginResult objRes = service.login(objReq);
            res.body(serializer.toJson(objRes));
        }
        catch(DataAccessException e){
            setResponse(e, res);
        }
        return res.body();
    }

    public Object logout(Request req, Response res){
        UserService service = new UserService(userDao, authDao);
        LogoutRequest objReq = new LogoutRequest(req.headers("authorization"));

        try{
            service.logout(objReq);
        }
        catch(DataAccessException e){
            setResponse(e, res);
            return res.body();
        }
        return "{}";
    }

    public Object createGame(Request req, Response res){
        GameService service = new GameService(gameDao, authDao);
        String auth = req.headers("authorization");
        CreateGameRequest objReq = serializer.fromJson(req.body(), CreateGameRequest.class);
        objReq.setAuthorization(auth);

        try{
            CreateGameResponse objRes = service.createGame(objReq);
            res.body(serializer.toJson(objRes));
        }
        catch(DataAccessException e){
            setResponse(e, res);
        }
        return res.body();
    }

    public Object joinGame(Request req, Response res){
        GameService service = new GameService(gameDao, authDao);
        JoinGameRequest objReq = serializer.fromJson(req.body(), JoinGameRequest.class);
        objReq.setAuthorization(req.headers("authorization"));
        try{
            service.joinGame(objReq);
        }
        catch(DataAccessException e){
            setResponse(e, res);
            return res.body();
        }
        return "{}";
    }

    public Object listGames(Request req, Response res){
        GameService service = new GameService(gameDao, authDao);
        String authorization = req.headers("authorization");
        try{
            ListGamesResponse objRes = service.listGames(authorization);
            res.body(serializer.toJson(objRes));
        }
        catch (DataAccessException e){
            setResponse(e, res);
        }
        return res.body();
    }


    private void setResponse(DataAccessException e, Response res){
        Message msg = new Message((e.getMessage()));
        res.body(serializer.toJson(msg));
        if(e.getMessage().equals("Error: unauthorized")){
            res.status(401);
        }
        else if(e.getMessage().equals("Error: bad request")){
            res.status(400);
        }
        else if(e.getMessage().equals("Error: already taken")){
            res.status(403);
        }
    }

}
