package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserCreds;

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
    public Response deleteUser(String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .when()
                .delete(USER_DEL_DELETE);
    }

    @Step("Login user {user}")
    public ValidatableResponse loginUser(UserCreds userCreds) {
        return given()
                .spec(getBaseReqSpec())
                .body(userCreds)
                .when()
                .post(USER_AUTH_POST)
                .then();
    }

    @Step("Update user data")
    public ValidatableResponse updateUserData(User user, String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", token)
                .and()
                .body(user)
                .when()
                .patch(USER_UPDATE_PATCH)
                .then();
    }
}
