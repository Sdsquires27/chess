package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import service.requests.AuthTokenRequest;

import java.util.Collection;
import java.util.List;

public class SQLDataAccess implements DataAccess{

    String authTableName = "auth";
    String userTableName = "user";
    String gameTableName = "game";
    public SQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var createAuthTable = String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        authToken VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL
                    );""", authTableName);
            var createUserTable = String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        username VARCHAR(255) NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL
                    );""", userTableName);
            var createGameTable = String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        authToken INT NOT NULL,
                        whiteUsername VARCHAR(255) NOT NULL,
                        blackUsername VARCHAR(255) NOT NULL,
                        chessGame VARCHAR(255) NOT NULL
                    );""", gameTableName);
            for (var statement : new String[] {createAuthTable, createUserTable, createGameTable}){
                try (var createTableStatement = conn.prepareStatement(statement)) {
                    createTableStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Create database failed");
        }
    }

    @Override
    public void clearDB() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            for (var tableName : new String[] {authTableName, gameTableName, userTableName}){
                try (var preparedStatement = conn.prepareStatement(String.format("TRUNCATE TABLE %s;", tableName))){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: Clear database: " + e.getMessage());
            throw new DataAccessException("Clear database failed");
        }
    }

    @Override
    public Boolean authorized(AuthTokenRequest request) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("""
                    SELECT EXISTS (
                        SELECT 1
                        FROM %s
                        WHERE authToken = ?
                    );
                    """, authTableName);
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, request.authToken());
                try (var result = preparedStatement.executeQuery()){
                    if (result.next()){
                        return result.getBoolean(1);
                    }
                    return false;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Authorized failed");
        }
    }

    @Override
    public void createGame(String gameName, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Create game failed");
        }
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Find game failed");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("List games failed");
        }
    }

    @Override
    public void deleteSession(AuthTokenRequest request) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("""
                    DELETE FROM %s WHERE authToken=?;
                    """, authTableName);
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, request.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Delete session failed");
        }
    }

    @Override
    public UserData findUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("""
                    SELECT username, password_hash, email
                    FROM %s
                    WHERE username = ?
                    """, userTableName);
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, username);
                try (var result = preparedStatement.executeQuery()){
                    if (result.next()) {
                        return new UserData(result.getString("username"),
                                result.getString("password_hash"),
                                result.getString("email"));
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: Find user: " + e.getMessage());
            throw new DataAccessException("Find user failed");
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("""
                    INSERT INTO %s (authToken, username)
                    VALUES (?, ?);
                    """, authTableName);
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Create auth failed");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("""
                    INSERT INTO %s (username, password_hash, email)
                    VALUES (?, ?, ?);
                    """, userTableName);
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }        } catch (Exception e) {
            throw new DataAccessException("Create user failed");
        }
    }

    @Override
    public void updateGame(int gameID, String game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            throw new RuntimeException("not implemented");
        } catch (Exception e) {
            throw new DataAccessException("Update game failed");
        }
    }

    @Override
    public String findUser(AuthTokenRequest request) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("""
                    SELECT username
                    FROM %s
                    WHERE authToken = ?
                    """, authTableName);
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, request.authToken());
                try (var result = preparedStatement.executeQuery()){
                    if (result.next()) {
                        return result.getString("username");
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Find user by auth failed");
        }
    }
}
