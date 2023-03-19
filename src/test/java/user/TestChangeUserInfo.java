package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserCredentials;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Изменение данных пользователя")
public class TestChangeUserInfo {

    private UserInfo userInfo;
    private UserInfo userInfoUpdate;
    private UserInfo userInfoSecond;
    private User userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userInfo = UserData.getUserData();
        userInfoUpdate = UserData.getUserUpdate();
        userInfoSecond = UserData.getUserAlreadyExisting();
        userClient = new User();
    }

    @Test
    @DisplayName("Изменение данных пользователя после успешной авторизацией")
    public void changeUserData() {
        userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseLogin = userClient.loginUser(UserCredentials.from(userInfo));
        accessToken = responseLogin.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUserData(accessToken, UserCredentials.from(userInfoUpdate));
        assertEquals(SC_OK, responseUpdateUser.extract().statusCode());
        assertTrue(responseUpdateUser.extract().path("success"));
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией без логина")
    public void changeUserDataWithoutLogin() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.from(userInfo));
        accessToken = responseCreate.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUserData("", UserCredentials.from(userInfoUpdate));
        assertEquals(SC_UNAUTHORIZED, responseUpdateUser.extract().statusCode());
        assertEquals("You should be authorised", responseUpdateUser.extract().path("message"));
    }

    @Test
    @DisplayName("Изменение данных пользователя с существующим email")
    public void changeUserDataExistEmail() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseCreateSecondUser = userClient.createUser(UserCredentials.from(userInfoSecond));
        accessToken = responseCreate.extract().path("accessToken");
        String accessTokenSecond = responseCreateSecondUser.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUserData(accessToken, UserCredentials.from(userInfoSecond));
        assertEquals(SC_FORBIDDEN, responseUpdateUser.extract().statusCode());
        assertEquals("User with such email already exists", responseUpdateUser.extract().path("message"));
        userClient.deleteUser(accessTokenSecond);
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}