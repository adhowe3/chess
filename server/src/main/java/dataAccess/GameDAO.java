package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameData();
    void add(GameData data);

    GameData getGameData(String gameName);

    GameData getGameDataFromID(int gameID);

    void updateWhiteUsername(int gameID, String username);

    void updateBlackUsername(int gameID, String username);

    int nextGameID();

    ArrayList<GameData> getAll();

}
