package order;

import data.Order;
import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;
import java.util.ArrayList;
import java.util.List;
import static order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class CreateOrderTest {

    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private Order order;

    private String accessToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        order = getListOrder();
        orderClient = new OrderClient();
    }

@Test
@DisplayName("Создание заказа с авторизацией")
@Description("Проверка создания заказа с авторизацией")
public void createOrderWithAuthorizationTest() {
    ValidatableResponse responseRegister = userClient.register(user);
    userClient.login(user);
    accessToken = responseRegister.extract()
            .path("accessToken");
    ValidatableResponse responseCreateOrder = orderClient.create(order, accessToken);
    responseCreateOrder.assertThat()
            .statusCode(SC_OK)
            .body("success", is(true));
}

    @Test
    @DisplayName("Создание заказа без авторизацией")
    @Description("Проверка создания заказа без авторизацией")
    public void createOrderWithoutAuthorizationTest() {
        accessToken = "";
        ValidatableResponse responseCreateOrder = orderClient.create(order, accessToken);
        responseCreateOrder
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов")
    @Description("Проверка создания заказа без ингридиентов")
    public void createOrderWithoutIngridientTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        accessToken = responseRegister.extract().path("accessToken");
        order.setIngredients(java.util.Collections.emptyList());
        ValidatableResponse responseCreateOrder = orderClient.create(order, accessToken);
        responseCreateOrder
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неправильными ингридиентами")
    @Description("Проверка создания заказа с неправильными ингридиентами")
    public void createOrderWithWrongIngridientTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        accessToken = responseRegister.extract().path("accessToken");
        List wrongIngridient = new ArrayList();
        wrongIngridient.add("50d3b41abdacab0026a733c9");
        order.setIngredients(wrongIngridient);
        ValidatableResponse responseCreateOrder = orderClient.create(order, accessToken);
        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .and()
                .body("message", is("One or more ids provided are incorrect"));
    }

    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken.equals("")) return;
        userClient.delete(accessToken);

    }
}
