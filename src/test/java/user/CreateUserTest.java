package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class CreateUserTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();

    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Позитивная проверка создания пользователя с валидными данными")
    public void createUserTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        accessToken = responseRegister.extract()
                .path("accessToken");
        responseRegister.assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание не уникального пользователя")
    @Description("Негативная проверка создания существующего пользователя")
    public void createAlreadyExistsUserTest() {
        ValidatableResponse responseRegisterFirstUser = userClient.register(user);
        accessToken = responseRegisterFirstUser.extract().path("accessToken");

        ValidatableResponse responseRegisterSecondUser = userClient.register(user);
        responseRegisterSecondUser.assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без поля Name")
    @Description("Негативная проверка создания пользователя без поля name")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse responseRegister = userClient.register(user);
        accessToken = responseRegister.extract()
                .path("accessToken");
        responseRegister.assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Негативная проверка создания пользователя без поля email")
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse responseRegister = userClient.register(user);
        accessToken = responseRegister.extract()
                .path("accessToken");
        responseRegister.assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без поля Password")
    @Description("Негативная проверка создания пользователя без поля password")
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse responseRegister = userClient.register(user);
        accessToken = responseRegister.extract()
                .path("accessToken");
        responseRegister.assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken == null) return;
        userClient.delete(accessToken);

    }
}
