package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserInfo;
import user.User;
import user.UserData;
import utils.UserCredentials;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Создание заказа")
public class TestCreateOrder {

    private UserInfo userInfo;
    private Ingredients ingredients;
    private Ingredients ingredientsEmpty;
    private Ingredients ingredientsIncorrect;
    private User userClient;
    private CreateOrder createOrder;
    private String accessToken;

    @Before
    public void setUp() {
        userInfo = UserData.getUserData();
        ingredients = OrderInfo.getIngredients();
        ingredientsEmpty = OrderInfo.getIngredientsEmpty();
        ingredientsIncorrect = OrderInfo.getIngredientsIncorrect();
        userClient = new User();
        createOrder = new CreateOrder();
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами с авторизацией пользователя")
    public void createOrderWithAuth() {
        userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseLogin = userClient.loginUser(UserCredentials.from(userInfo));
        accessToken = responseLogin.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = createOrder.createOrder(ingredients, accessToken);
        assertEquals(SC_OK, responseOrderCreate.extract().statusCode());
        assertTrue(responseOrderCreate.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами без авторизации пользователя")
    public void createOrderWithoutAuth() {
        ValidatableResponse responseUserCreate = userClient.createUser(UserCredentials.from(userInfo));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = createOrder.createOrderWithoutAuthorization(ingredients);
        assertEquals(SC_OK, responseOrderCreate.extract().statusCode());
        assertTrue(responseOrderCreate.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        ValidatableResponse responseUserCreate = userClient.createUser(UserCredentials.from(userInfo));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = createOrder.createOrderWithoutAuthorization(ingredientsEmpty);
        assertEquals(SC_BAD_REQUEST, responseOrderCreate.extract().statusCode());
        assertEquals("Ingredient ids must be provided", responseOrderCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithIncorrectHash() {
        ValidatableResponse responseUserCreate = userClient.createUser(UserCredentials.from(userInfo));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = createOrder.createOrderWithoutAuthorization(ingredientsIncorrect);
        assertEquals(SC_INTERNAL_SERVER_ERROR, responseOrderCreate.extract().statusCode());
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}