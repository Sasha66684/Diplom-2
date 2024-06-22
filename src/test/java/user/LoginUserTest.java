package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class LoginUserTest {

    private UserClient userClient;
    private User user;
    private String accessToken;
    private ValidatableResponse responseRegister;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        responseRegister = userClient.register(user);


    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    @Description("Позитивная проверка авторизации пользователя")
    public void loginUser() {
        accessToken = responseRegister.extract().
                path("accessToken");
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Авторизация с неверным полем Password пользователя")
    @Description("Негативная проверка авторизации пользователя с неверным полем password")
    public void loginUserWithWrongPass() {
        accessToken = responseRegister.extract()
                .path("accessToken");
        user.setPassword("");
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным полем Email пользователя")
    @Description("Негативная проверка авторизации пользователя с неверным полем email")
    public void loginUserWithWrongEmail() {
        accessToken = responseRegister.extract()
                .path("accessToken");
        user.setEmail("");
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken == null) return;
        userClient.delete(accessToken);

    }
}
