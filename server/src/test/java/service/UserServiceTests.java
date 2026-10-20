package service;

import dataaccess.DataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.*;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {

    FakeDataAccess dataAccess = new FakeDataAccess();
    UserService userService = new UserService(dataAccess);

    @BeforeEach
    public void resetAccess() {
        dataAccess.resetAccess();
    }

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
        assertMethodsAccessed(new String[] {"createAuth", "findUser", "createUser"});
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
        assertMethodsAccessed(new String[] {"createAuth", "findUser"});
    }

    @Test
    void logout() {
        assertThrows(UnauthorizedException.class,
                () -> userService.Logout(new AuthTokenRequest("FakeAuth")));
        assertDoesNotThrow(() -> userService.Logout(new AuthTokenRequest(dataAccess.defaultAuth)));
        assertMethodsAccessed(new String[] {"deleteSession", "authorized"});
        assert(Objects.equals(dataAccess.defaultAuth, dataAccess.authToken));
    }

    void assertMethodsAccessed(String[] methods) {
        for (var method : dataAccess.methods) {
            if (Arrays.asList(methods).contains(method)) {
                assert(dataAccess.calledMethod.get(method));
                continue;
            }
            assert(!dataAccess.calledMethod.get(method));
        }
    }
}