package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import site.nomoreparties.stellarburgers.model.Order;

import static io.restassured.RestAssured.given;
import static site.nomoreparties.stellarburgers.constants.Endpoints.*;

public class OrderSteps {
    public static RequestSpecification getBaseReqSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URI)
                .build();
    }
    @Step("Create new order")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .and()
                .body(order)
                .when()
                .post(ORDER_CREATE_POST)
                .then();
    }
}
