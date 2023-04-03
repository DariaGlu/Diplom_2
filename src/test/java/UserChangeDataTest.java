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

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class UserChangeDataTest {
    private final static String NO_ACCESS_TOKEN = "";
    private User user;
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
    @DisplayName("Change user email, authorized")
    public void changeEmailAuthorizedSuccess() {
        user.setEmail(RandomStringUtils.randomAlphanumeric(3, 10).toLowerCase() + "@yandex.ru");
        userSteps.updateUserData(user, accessToken)
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Change user password, authorized")
    public void changePasswordAuthorizedSuccess() {
        user.setPassword(RandomStringUtils.randomAlphanumeric(6, 12));
        userSteps.updateUserData(user, accessToken)
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Change user name, authorized")
    public void changeNameAuthorizedSuccess() {
        user.setName(RandomStringUtils.randomAlphanumeric(3, 10));
        userSteps.updateUserData(user, accessToken)
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Change user email, unauthorized")
    public void changeEmailUnauthorizedFail() {
        user.setEmail(RandomStringUtils.randomAlphanumeric(3, 10).toLowerCase() + "@yandex.ru");
        userSteps.updateUserData(user, NO_ACCESS_TOKEN)
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false), "message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Change user password, unauthorized")
    public void changePasswordUnauthorizedFail() {
        user.setPassword(RandomStringUtils.randomAlphanumeric(6, 12));
        userSteps.updateUserData(user, NO_ACCESS_TOKEN)
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false), "message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Change user name, unauthorized")
    public void changeNameUnauthorizedFail() {
        user.setName(RandomStringUtils.randomAlphanumeric(3, 10));
        userSteps.updateUserData(user, NO_ACCESS_TOKEN)
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false), "message", is("You should be authorised"));
    }
}
