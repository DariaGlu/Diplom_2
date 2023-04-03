package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import site.nomoreparties.stellarburgers.model.User;

import static io.restassured.RestAssured.given;
import static site.nomoreparties.stellarburgers.constants.Endpoints.*;

public class UserSteps {
    public static RequestSpecification getBaseReqSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URI)
                .build();
    }
    @Step("Create new user {user}")
    public Response createUser(User user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(USER_REGISTER_POST);
    }
    @Step("Delete user {user}")
    public Response deleteUser(User user, String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .delete(USER_DEL_DELETE);
    }
}
