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

import java.util.ArrayList;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

@DisplayName("Получение заказов")
public class TestUserCreateOrder {

    private UserInfo userInfo;
    private Ingredients ingredients;
    private User userClient;
    private CreateOrder createOrder;
    private String accessToken;

    @Before
    public void setUp() {
        userInfo = UserData.getUserData();
        ingredients = OrderInfo.getIngredients();
        userClient = new User();
        createOrder = new CreateOrder();
    }

    @Test
    @DisplayName("Получение заказов - авторизованный пользователь")
    public void getAuthorizedUserOrders() {
        int totalOrder = 1;
        userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseLogin = userClient.loginUser(UserCredentials.from(userInfo));
        accessToken = responseLogin.extract().path("accessToken");
        createOrder.createOrder(ingredients, accessToken);
        ValidatableResponse responseGetOrders = createOrder.getOrdersForUser(accessToken);
        ArrayList<Object> orders = responseGetOrders.extract().path("orders");
        assertEquals(SC_OK, responseGetOrders.extract().statusCode());
        assertTrue(responseGetOrders.extract().path("success"));
        assertEquals(totalOrder, orders.size());
    }

    @Test
    @DisplayName("Получение заказов - неавторизованный пользователь")
    public void getUnauthorizedUserOrders() {
        userClient.createUser(UserCredentials.from(userInfo));
        ValidatableResponse responseLogin = userClient.loginUser(UserCredentials.from(userInfo));
        accessToken = responseLogin.extract().path("accessToken");
        createOrder.createOrder(ingredients, accessToken);
        ValidatableResponse responseGetOrders = createOrder.getOrdersForUser("");
        assertEquals(SC_UNAUTHORIZED, responseGetOrders.extract().statusCode());
        assertFalse(responseGetOrders.extract().path("success"));
        assertEquals("You should be authorised", responseGetOrders.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}