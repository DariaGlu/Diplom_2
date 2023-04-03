package user;

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
import site.nomoreparties.stellarburgers.model.UserCreds;
import site.nomoreparties.stellarburgers.steps.UserSteps;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class UserAuthTest {
    private User user;
    private UserCreds userCreds;
    private UserSteps userSteps;
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
    }

    @After
    public void cleanUp() {
        userSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("User logs in with his data")
    public void userLoginSuccess() {
        userSteps.loginUser(userCreds.from(user))
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("User logs with wrong email")
    public void userLoginWrongEmailFail() {
        user.setEmail(RandomStringUtils.randomAlphanumeric(3, 10).toLowerCase() + "@yandex.ru");
        userSteps.loginUser(userCreds.from(user))
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false), "message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("User logs with wrong password")
    public void userLoginWrongPasswordFail() {
        user.setPassword(RandomStringUtils.randomAlphanumeric(6, 12));
        userSteps.loginUser(userCreds.from(user))
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false), "message", is("email or password are incorrect"));
    }
}
