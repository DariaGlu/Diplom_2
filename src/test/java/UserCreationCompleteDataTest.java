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
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.steps.UserSteps;

import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UserCreationCompleteDataTest {
    private User user;
    private UserSteps userSteps;
    private String token;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter()
        );
    }

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        String email = RandomStringUtils.randomAlphanumeric(3, 10).toLowerCase(Locale.ROOT) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphanumeric(6, 12);
        String name = RandomStringUtils.randomAlphanumeric(3, 10);
        user = new User(email, password, name);
    }

    @After
    public void cleanUp() {
        userSteps.deleteUser(user, token);
    }

    @Test
    @DisplayName("Create new user with unique email")
    public void createUniqueUserSuccess() {
        Response response = userSteps.createUser(user);
        token = response.then().extract().path("accessToken");
        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create new user with already existing email")
    public void createNotUniqueUserFail() {
        Response response = userSteps.createUser(user);
        token = response.then().extract().path("accessToken");
        userSteps.createUser(user)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", equalTo(false), "message", is("User already exists"));
    }
}
