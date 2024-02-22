package handlers;
import com.google.gson.Gson;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.Message;
import model.RegisterRequest;
import model.RegisterResult;
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
        DbService service = new DbService();
        service.clear(authDao, gameDao, userDao);
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
            if(e.getMessage().equals("Error: already taken")){
                Message msg = new Message(e.getMessage());
                res.status(403);
                res.body(serializer.toJson(msg));
            }

        }
        System.out.println(res.body());
        return res.body();
    }
}
