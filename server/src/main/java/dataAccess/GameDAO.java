package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameData();
    void add(GameData data);

    public ArrayList<GameData> getAll();

}
