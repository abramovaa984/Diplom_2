package user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import utils.UserSettings;
import utils.UserCredentials;

import static io.restassured.RestAssured.given;

public class User extends UserSettings {
    private static final String CREATE = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";
    private static final String CHANGE_USER = "/api/auth/user";
    private static final String DELETE = "/api/auth/user";

    @Step("Создание пользователя")
    public ValidatableResponse createUser(UserCredentials userCredentials) {
        return given()
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .post(CREATE)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        return given()
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .post(LOGIN)
                .then();
    }

    @Step("Изменить данные пользователя с авторизацией")
    public ValidatableResponse changeUserData(String accessToken, UserCredentials userCredentials) {
        return given()
                .header("Authorization", accessToken)
                .header("Accept", "*/*")
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .patch(CHANGE_USER)
                .then();
    }

    @Step("Удалить пользователя")
    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .header("Accept", "*/*")
                    .spec(getSpecForDelete())
                    .delete(DELETE)
                    .then();
        }
    }
}