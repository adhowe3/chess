package dataAccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameData() throws DataAccessException;
    void add(GameData data) throws DataAccessException;
    void updateGame(GameData data) throws DataAccessException;
    GameData getGameDataFromID(int gameID) throws DataAccessException;
    void updateWhiteUsername(int gameID, String username) throws DataAccessException;
    void updateBlackUsername(int gameID, String username) throws DataAccessException;
    int nextGameID() throws  DataAccessException;
    ArrayList<GameData> getAll() throws  DataAccessException;
}
