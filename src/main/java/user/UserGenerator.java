package user;

import data.User;

import static service.RandomUtils.randomString;

public class UserGenerator {
    public static User getRandomUser() {

        return new User()
                .setEmail(randomString(9) + "@gmail.com")
                .setPassword(randomString(6))
                .setName(randomString(5));

    }
}