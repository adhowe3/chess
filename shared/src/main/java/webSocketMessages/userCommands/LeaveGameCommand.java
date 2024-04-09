package webSocketMessages.userCommands;

public class LeaveGameCommand extends UserGameCommand{
    public LeaveGameCommand(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }

}
