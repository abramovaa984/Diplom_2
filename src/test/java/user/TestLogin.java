package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserCredentials;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Логин пользователя")
public class TestLogin {
    private UserInfo userInfo;
    private UserInfo userInfoIncorrect;
    private User userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userInfo = UserData.getUserData();
        userInfoIncorrect = UserData.getUserDataIncorrect();
        userClient = new User();
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void loginUser() {
        userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseLogin = userClient.loginUser(UserCredentials.from(userInfo));
        accessToken = responseLogin.extract().path("accessToken");
        assertEquals(SC_OK, responseLogin.extract().statusCode());
        assertTrue(responseLogin.extract().path("success"));
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void loginUserWithIncorrectCredentials() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseLogin = userClient.loginUser(UserCredentials.from(userInfoIncorrect));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_UNAUTHORIZED, responseLogin.extract().statusCode());
        assertEquals("email or password are incorrect", responseLogin.extract().path("message"));
    }

    @Test
    @DisplayName("Логин с пустым полем пароля")
    public void loginUserWithoutPassword() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseLogin = userClient.loginUser(UserCredentials.fromOnlyEmail(userInfo));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_UNAUTHORIZED, responseLogin.extract().statusCode());
        assertEquals("email or password are incorrect", responseLogin.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}