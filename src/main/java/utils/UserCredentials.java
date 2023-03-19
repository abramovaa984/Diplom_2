package utils;

import lombok.Data;
import user.UserInfo;

@Data
public class UserCredentials {
    private String name;
    private String email;
    private String password;

    public UserCredentials(String email, String password, String name) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static UserCredentials from(UserInfo userInfo) {
        return new UserCredentials(userInfo.getEmail(), userInfo.getPassword(), userInfo.getName());
    }

    public static UserCredentials fromOnlyEmailAndPassword(UserInfo userInfo) {
        return new UserCredentials(userInfo.getEmail(), userInfo.getPassword(), "");
    }

    public static UserCredentials fromOnlyEmail(UserInfo userInfo) {
        return new UserCredentials(userInfo.getEmail(), "", "");
    }

    public static UserCredentials fromOnlyPassword(UserInfo userInfo) {
        return new UserCredentials("", userInfo.getPassword(), "");
    }
}