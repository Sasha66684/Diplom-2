package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class ChangeDataUserTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверка изменения данных существующего пользователя с авторизацией")
    public void changeDataUserWithAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);
        accessToken = responseRegister
                .extract()
                .path("accessToken");
        User secondUser = getRandomUser();
        ValidatableResponse responsePatch = userClient.patch(secondUser, accessToken);
                responsePatch.assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка изменения данных существующего пользователя без авторизации")
    public void changeDataUserWithoutAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);
        accessToken = responseRegister
                .extract()
                .path("accessToken");
        User secondUser = getRandomUser();
        ValidatableResponse responsePatch = userClient.patch(secondUser, accessToken);
        responsePatch.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and()
                .body("message", is("You should be authorised"));
    }
    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken == null) return;
        userClient.delete(accessToken);
    }
}