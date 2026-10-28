package service;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.requests.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests extends ServiceTests{

    UserService userService = new UserService(dataAccess);

    @Test
    void registerUser() {
        UserData newUser = new UserData("newUser", "password", "email");
        assertThrows(InvalidInputException.class,
                () -> userService.registerUser(new RegisterRequest(null, "test", "test")));
        assertThrows(InvalidInputException.class,
                () -> userService.registerUser(new RegisterRequest("", "test", "test")));
        assertThrows(AlreadyTakenException.class,
                () -> userService.registerUser(new RegisterRequest(
                        dataAccess.defaultUser.username(),
                        "password",
                        dataAccess.defaultUser.email()
                )));
        assertDoesNotThrow(() -> userService.registerUser(new RegisterRequest(
                "newUser", "password", "email")));
        assertEquals(new UserData(dataAccess.userData.username(), "password",
                dataAccess.userData.email()), newUser);
        assertMethodsAccessed(new String[] {"createAuth", "findUser", "createUser"});
    }

    @Test
    void loginUser() {
        assertThrows(InvalidInputException.class,
                () -> userService.loginUser(new LoginRequest("", "test")));
        assertThrows(UnauthorizedException.class,
                () -> userService.loginUser(new LoginRequest("FakeUser", "Test")));
        assertThrows(UnauthorizedException.class,
                () -> userService.loginUser(new LoginRequest(dataAccess.defaultUser.username(), "fake_password")));
        assertDoesNotThrow(() -> userService.loginUser(new LoginRequest(dataAccess.defaultUser.username(),
                "password")));
        assertMethodsAccessed(new String[] {"createAuth", "findUser"});
    }

    @Test
    void logout() {
        assertThrows(UnauthorizedException.class,
                () -> userService.logout(new AuthTokenRequest("FakeAuth")));
        assertDoesNotThrow(() -> userService.logout(new AuthTokenRequest(dataAccess.defaultAuth)));
        assertMethodsAccessed(new String[] {"deleteSession", "authorized"});
        assertEquals(dataAccess.defaultAuth, dataAccess.authToken);
    }


}