package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;

public class SQLDataAccess implements DataAccess{

    public SQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void clearDB() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public Boolean authorized(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public void createGame(String gameName, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public void deleteSession(String authToken) throws DataAccessException {

    }

    @Override
    public UserData findUser(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public void updateGame(int gameID, String game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public String findUser(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Clear database failed");
        }
    }
}
