package service;

import dataaccess.DataAccess;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.requests.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {

    FakeDataAccess dataAccess = new FakeDataAccess();
    UserService userService = new UserService(dataAccess);

    @Test
    void registerUser() {
        UserData newUser = new UserData("newUser", "password", "email");
        assertThrows(InvalidInputException.class,
                () -> userService.RegisterUser(new RegisterRequest(null, "test", "test")));
        assertThrows(InvalidInputException.class,
                () -> userService.RegisterUser(new RegisterRequest("", "test", "test")));
        assertThrows(AlreadyTakenException.class,
                () -> userService.RegisterUser(new RegisterRequest(
                        dataAccess.defaultUser.username(),
                        dataAccess.defaultUser.password(),
                        dataAccess.defaultUser.email()
                )));
        assertDoesNotThrow(() -> userService.RegisterUser(new RegisterRequest(
                "newUser", "password", "email")));
        assert(Objects.equals(dataAccess.userData, newUser));
        assert(dataAccess.calledMethod.get("findUser"));
        assert(dataAccess.calledMethod.get("createUser"));
        assert(dataAccess.calledMethod.get("createAuth"));
    }

    @Test
    void loginUser() {
        assertThrows(InvalidInputException.class,
                () -> userService.LoginUser(new LoginRequest("", "test")));
        assertThrows(UnauthorizedException.class,
                () -> userService.LoginUser(new LoginRequest("FakeUser", "Test")));
        assertThrows(UnauthorizedException.class,
                () -> userService.LoginUser(new LoginRequest(dataAccess.defaultUser.username(), "fake_password")));
        assertDoesNotThrow(() -> userService.LoginUser(new LoginRequest(dataAccess.defaultUser.username(),
                dataAccess.defaultUser.password())));
        assert(dataAccess.calledMethod.get("createAuth"));
        assert(dataAccess.calledMethod.get("findUser"));
    }

    @Test
    void logout() {
    }
}