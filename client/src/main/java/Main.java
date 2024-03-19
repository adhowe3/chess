import ui.UserInterface;

public class Main {
    public static void main(String[] args) throws Exception{
        var serverUrl = "http://localhost:8080";
        UserInterface loginUserInterface = new UserInterface(serverUrl);
        loginUserInterface.runClient();
    }
}