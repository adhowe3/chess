package dataAccess;
import model.GameData;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    private ArrayList<GameData> gameData;

    public MemoryGameDAO(){
        gameData = new ArrayList<>();
    }

    @Override
    public void clearGameData(){
        gameData.clear();
    }
}
