package dataAccess;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    private ArrayList<GameData> gameData;

    public MemoryGameDAO(){
        gameData = new ArrayList<>();
    }

    @Override
    public void clearGameData(){
        gameData.clear();
    }

    @Override
    public void add(GameData data){
        this.gameData.add(data);
    }

    @Override
    public GameData getGameData(String gameName){
        for(GameData game : gameData){
            if(game.getGameName().equals(gameName))
                    return game;
        }
        return null;
    }

    @Override
    public GameData getGameDataFromID(int gameID){
        for(GameData game : gameData){
            if(game.getGameID() == gameID)
                return game;
        }
        return null;
    }

    @Override
    public void updateBlackUsername(int gameID, String username){
        getGameDataFromID(gameID).setBlackUsername(username);
    }

    @Override
    public void updateWhiteUsername(int gameID, String username){
        getGameDataFromID(gameID).setWhiteUsername(username);

    }

    @Override
    public int nextGameID(){
        return gameData.size() + 1;
    }

    public ArrayList<GameData> getAll(){
        return gameData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryGameDAO that = (MemoryGameDAO) o;
        return Objects.equals(gameData, that.gameData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameData);
    }
}
