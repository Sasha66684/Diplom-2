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
import static order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class GetOrderTest {

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
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка получения списка заказов авторизованного пользователя")
    public void createOrderWithAuthorizationTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        accessToken = responseRegister.extract()
                .path("accessToken");
        userClient.login(user);
        orderClient.create(order, accessToken);
        ValidatableResponse responseOrderUser = orderClient.getClientOrder(accessToken);
        responseOrderUser
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Проверка получения списка заказов неавторизованного пользователя")
    public void createOrderWithoutAuthorizationTest() {
        accessToken = "";
        ValidatableResponse getClientOrder = orderClient.getClientOrder(accessToken);
        getClientOrder.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and()
                .body("message", is("You should be authorised"));
    }


    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken.equals("")) return;
        userClient.delete(accessToken);

    }
}
