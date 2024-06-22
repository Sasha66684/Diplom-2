package service;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomUtils {

    public static String randomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

}
