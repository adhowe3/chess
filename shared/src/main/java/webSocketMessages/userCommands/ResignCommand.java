package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    public ResignCommand(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }

}
