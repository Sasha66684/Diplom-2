package user;

import io.qameta.allure.restassured.AllureRestAssured;
import service.Endpoints;
import service.Specifications;
import data.User;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.Matchers.is;
import static service.Endpoints.PATCH_USER;

public class UserClient {
    @Step("Создание пользователя. Send post request to api/auth/register")
    public ValidatableResponse register(User user) {
        return given()
                .filter(new AllureRestAssured())
                .spec(Specifications.requestSpecification())
                .and()
                .body(user)
                .when()
                .post(Endpoints.CREATE_USER)
                .then();
    }

    @Step("Авторизация пользователя. Send post request to api/auth/login ")
    public ValidatableResponse login(User user) {
        return given()
                .filter(new AllureRestAssured())
                .spec(Specifications.requestSpecification())
                .and()
                .body(user)
                .when()
                .post(Endpoints.LOGIN_USER)
                .then();
    }

    @Step("Удаление пользователя. Send delete request to api/auth/user")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(Specifications.requestSpecification())
                .headers("Authorization", accessToken)
                .delete(Endpoints.DELETE_USER)
                .then()
                .statusCode(SC_ACCEPTED)
                .and()
                .body("message", is("User successfully removed"));
    }

    @Step("Обновление данных пользователя с авторизацией. Send patch request to api/auth/user" )
    public ValidatableResponse patch(User user, String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(Specifications.requestSpecification())
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .patch(PATCH_USER)
                .then();
    }
}
