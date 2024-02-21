package handlers;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import spark.Response;
import spark.Request;
import service.DbService;
import dataAccess.*;
public class Handler {

    private GameDAO gameDao = new MemoryGameDAO();
    private AuthDAO authDao = new MemoryAuthDAO();
    private UserDAO userDao = new MemoryUserDAO();
    public Handler(){}

    public Object clear(Request req, Response res){
        DbService service = new DbService();
        service.clear(authDao, gameDao, userDao);
        return res.body();
    }
}
