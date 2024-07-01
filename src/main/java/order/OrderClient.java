package order;


import io.qameta.allure.restassured.AllureRestAssured;
import service.Endpoints;
import service.Specifications;
import data.Order;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;


public class OrderClient  {
    @Step("Создание заказа с авторизацией.")
    public ValidatableResponse create(Order order, String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(Specifications.requestSpecification())
                .headers("Authorization", accessToken)
                .and()
                .body(order)
                .when()
                .post(Endpoints.CREATE_ORDER)
                .then();
    }

        @Step("Получение данных об ингредиентахю Send get request to api/ingredients/")
        public static ValidatableResponse getAllIngredients () {
            return given()
                    .filter(new AllureRestAssured())
                    .filter(new AllureRestAssured())
                    .spec(Specifications.requestSpecification())
                    .get(Endpoints.ALL_INGREDIENT)
                    .then();
        }

        @Step("Получать запросы на заказы")
        public static ValidatableResponse getClientOrder (String accessToken){
            return given()
                    .filter(new AllureRestAssured())
                    .spec(Specifications.requestSpecification())
                    .headers("Authorization", accessToken)
                    .get(Endpoints.USER_ORDERS)
                    .then();
        }
    }
