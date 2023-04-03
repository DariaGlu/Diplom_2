import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.steps.UserSteps;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class UserCreationIncompleteDataTest {
    private User user;
    private UserSteps userSteps;
    private final String email;
    private final String password;
    private final String name;

    public UserCreationIncompleteDataTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "Test data: email - {0}, password - {1}, name - {2}")
    public static Object[][] getData() {
        return new Object[][]{
                {"", "password123", "Василиса"},
                {"vasilisa_prekrasnaya@mail.ru", "", "Василиса"},
                {"vasilisa_prekrasnaya@mail.ru", "password123", ""}
        };
    }

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter()
        );
    }

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        user = new User(email, password, name);
    }

    @Test
    @DisplayName("Create user without one of required fields")
    public void createUserIncompleteDataFail() {
        userSteps.createUser(user)
                .then()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", equalTo(false), "message", is("Email, password and name are required fields"));
    }
}
