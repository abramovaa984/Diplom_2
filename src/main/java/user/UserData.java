package user;

import org.apache.commons.lang3.RandomStringUtils;

public class UserData {
    private static final int COUNT = 10;
    private static final int COUNT_INCORRECT_PASSWORD = 2;

    public static String generateString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static UserInfo getUserData() {
        return new UserInfo(generateString(COUNT) + "@gmail.com", generateString(COUNT), generateString(COUNT));
    }

    public static UserInfo getUserDataIncorrect() {
        return new UserInfo(generateString(COUNT) + "@gmail.com", generateString(COUNT_INCORRECT_PASSWORD), generateString(COUNT));
    }

    public static UserInfo getUserUpdate() {
        return new UserInfo(generateString(COUNT) + "@gmail.com", generateString(COUNT), generateString(COUNT));
    }

    public static UserInfo getUserAlreadyExisting() {
        return new UserInfo(generateString(COUNT) + "@gmail.com", generateString(COUNT), generateString(COUNT));
    }
}