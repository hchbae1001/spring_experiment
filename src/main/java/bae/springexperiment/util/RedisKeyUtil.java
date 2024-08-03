package bae.springexperiment.util;

public class RedisKeyUtil {
    private static final String ACCESS_TOKEN_KEY_FORMAT = "accessToken::%s::deviceType::%s";
    private static final String MEMBER_KEY_FORMAT = "Member::%s";
    private static final String BLACKLIST_KEY_FORMAT = "blacklist::%s";

    public static String generateAccessTokenKey(Long memberId, String deviceType) {
        return String.format(ACCESS_TOKEN_KEY_FORMAT, memberId, deviceType);
    }

    public static String generateBlackListKey(String accessToken){
        return String.format(BLACKLIST_KEY_FORMAT, accessToken);
    }

    public static String generateMemberKey(Long memberId) {
        return String.format(MEMBER_KEY_FORMAT, memberId);
    }
}
