package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserCredentials;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

@DisplayName("Создание пользователя")
public class TestCreateUser {

    private UserInfo userInfo;
    private User userClient;
    private String accessToken;
    private String accessTokenError;

    @Before
    public void setUp() {
        userInfo = UserData.getUserData();
        userClient = new User();
        accessTokenError = null;
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUser() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.from(userInfo));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_OK, responseCreate.extract().statusCode());
    }

    @Test
    @DisplayName("Пользователь не уникального пользователя")
    public void createNonUniqueUser() {
        ValidatableResponse responseCreateUniqueUser = userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseCreateExistUser = userClient.createUser(UserCredentials.from(userInfo));
        accessToken = responseCreateUniqueUser.extract().path("accessToken");
        if (responseCreateExistUser.extract().path("accessToken") != null) {
            accessTokenError = responseCreateExistUser.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreateExistUser.extract().statusCode());
        assertEquals("User already exists", responseCreateExistUser.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина и пароля")
    public void createUserWithoutLoginAndPassword() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.fromOnlyEmail(userInfo));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина и email")
    public void createUserWithoutLoginAndEmail() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.fromOnlyPassword(userInfo));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина")
    public void createUserWithoutLogin() {
        ValidatableResponse responseCreate = userClient.createUser(UserCredentials.fromOnlyEmailAndPassword(userInfo));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
        if (accessTokenError != null) {
            userClient.deleteUser(accessTokenError);
        }
    }
}