package requests;

public class CreateGameRequest {

    public CreateGameRequest(String a, String n){
        authorization = a;
        gameName = n;
    }
    private String authorization;
    private String gameName;

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
