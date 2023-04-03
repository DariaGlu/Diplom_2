package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import site.nomoreparties.stellarburgers.model.Order;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.steps.OrderSteps;
import site.nomoreparties.stellarburgers.steps.UserSteps;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;

public class OrderGetListTest {
    private static final String INGREDIENT_BUN_ID = "61c0c5a71d1f82001bdaaa6d";
    private static final String INGREDIENT_FILLING_ID = "61c0c5a71d1f82001bdaaa6f";
    private final static String NO_ACCESS_TOKEN = "";
    private final List<String> ingredients = new ArrayList<>();
    private User user;
    private UserSteps userSteps;
    private Order order;
    private OrderSteps orderSteps;
    private String accessToken;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter()
        );
    }

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        String email = RandomStringUtils.randomAlphanumeric(3, 10).toLowerCase() + "@yandex.ru";
        String password = RandomStringUtils.randomAlphanumeric(6, 12);
        String name = RandomStringUtils.randomAlphanumeric(3, 10);
        user = new User(email, password, name);
        Response response = userSteps.createUser(user);
        accessToken = response.then().extract().path("accessToken");
        orderSteps = new OrderSteps();
    }

    @After
    public void cleanUp() {
        userSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Get list of orders of authorized user")
    public void getListOfOrdersAuthorizedUserSuccess() {
        ingredients.add(INGREDIENT_BUN_ID);
        ingredients.add(INGREDIENT_FILLING_ID);
        order = new Order(ingredients);
        orderSteps.createOrder(order, accessToken);
        orderSteps.getListOfOrders(accessToken)
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true), "orders", notNullValue());
    }

    @Test
    @DisplayName("Get list of orders of unauthorized user")
    public void getListOfOrdersUnauthorizedUserFail() {
        ingredients.add(INGREDIENT_BUN_ID);
        ingredients.add(INGREDIENT_FILLING_ID);
        order = new Order(ingredients);
        orderSteps.createOrder(order, accessToken);
        orderSteps.getListOfOrders(NO_ACCESS_TOKEN)
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false), "message", is("You should be authorised"));
    }
}
