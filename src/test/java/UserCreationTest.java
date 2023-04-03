import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserCreds;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static site.nomoreparties.stellarburgers.constants.Endpoints.USER_DEL_DELETE;
import static site.nomoreparties.stellarburgers.constants.Endpoints.USER_REGISTER_POST;
import static site.nomoreparties.stellarburgers.steps.UserSteps.getBaseReqSpec;

public class UserCreationTest {
    private User user;
    private String email = "test_daria2@test.ru";
    private String password = "test1test";
    private String name = "test_daria";
    private String token;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter()
        );
    }

    @Before
    public void setUp() {
        user = new User(email, password, name);
    }
    @After
    public void cleanUp() {
        given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .delete(USER_DEL_DELETE);
    }

    @Test
    public void createUniqueUserSuccess() {
        Response response = given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(USER_REGISTER_POST);
        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
        token = response.then().extract().path("accessToken");
    }
}
