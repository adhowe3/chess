package handlers;
import com.google.gson.Gson;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.*;
import service.UserService;
import spark.Response;
import spark.Request;
import service.DbService;
import dataAccess.*;
public class Handler {
    private GameDAO gameDao = new MemoryGameDAO();
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();
    Gson serializer = new Gson();
    public Handler(){}

    public Object clear(Request req, Response res){
        DbService service = new DbService(authDao, gameDao, userDao);
        service.clear();
        res.status(200);
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
            Message msg = new Message(e.getMessage());
            res.body(serializer.toJson(msg));
            if(e.getMessage().equals("Error: already taken")){
                res.status(403);
            }
            if(e.getMessage().equals("Error: bad request")){
                res.status(400);
            }

        }
        System.out.println(res.body());
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
            Message msg = new Message(e.getMessage());
            res.body(serializer.toJson(msg));
            if(e.getMessage().equals("Error: unauthorized")){
                res.status(401);
            }
        }
        System.out.println(res.body());
        return res.body();
    }

}
