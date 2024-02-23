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
