package redis.spike.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import redis.clients.jedis.Jedis;
import redis.spike.demo.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestServiceConfiguration.class})
class DemoApplicationTests {


    private static final String MEMBER_USERNAME = "tiap.user1@invalid.com";
    private static final String MEMBER_PASSWORD = "test1234";

    private static final String DEVICE_USERNAME = "f73f82ce-38a6-47ca-b85d-bb5a1bdb289c";
    private static final String DEVICE_PASSWORD = "ABCD12345";

    public static final String REFRESH_REDIS_SET_NAME = "refresh:";
    public static final String ACCESS_REDIS_SET_NAME = "access:";
    public static final String AUTH_REDIS_SET_NAME = "auth:";

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;


    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private RedisTemplate<String, OAuth2RefreshToken> refreshTokenRedisTemplate;

    @Autowired
    private RedisTemplate<String, OAuth2AccessToken> accessTokenRedisTemplate;

    @Autowired
    private RedisTemplate<String, String> authRedisTemplate;

    @Autowired
    private Jedis jedis;

    @BeforeEach
    public void setUp() throws Exception {
        jedis.flushDB();
        base = new URL("http://localhost:" + port);
    }


    /////

    @Test
    public void creatingAValidDeviceTokenReturns200WithTokenPayload() {
        final var response = requestDeviceToken(DEVICE_USERNAME, DEVICE_PASSWORD);
        final var oAuth2AccessToken = response.getBody();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(oAuth2AccessToken);
    }


    @Test
    public void requestingAnAuthTokenFromARefreshTokenReturnsANewDeviceToken() {
        final var deviceToken = createDeviceToken(DEVICE_USERNAME, DEVICE_PASSWORD);
        final var refreshToken = deviceToken.getRefreshToken().getValue();
        final var deviceTokenFromRefreshToken = requestOauth2AccessTokenFromRefreshToken(refreshToken);

        assertNotNull(refreshToken);
        assertNotNull(deviceTokenFromRefreshToken.getValue());
        assertEquals(deviceToken.getAdditionalInformation(), deviceTokenFromRefreshToken.getAdditionalInformation());
    }




    /////////////////////////////////////////

    private OAuth2AccessToken requestOauth2AccessTokenFromRefreshToken(String refreshToken) {
        HttpEntity<String> httpEntity = new HttpEntity<>("grant_type=refresh_token&refresh_token=" + refreshToken, getHttpHeaders());

        ResponseEntity<OAuth2AccessToken> response = template.postForEntity(
            base.toString() + "/oauth/token",
            httpEntity,
            OAuth2AccessToken.class
        );

        return response.getBody();
    }

    private OAuth2AccessToken createDeviceToken(String deviceUsername, String devicePassword) {
        return requestDeviceToken(deviceUsername, devicePassword).getBody();
    }

    private ResponseEntity<OAuth2AccessToken> requestDeviceToken(String deviceUsername, String devicePassword) {
        User testAcc = new User();
        testAcc.setId(123);
        testAcc.setUsername(deviceUsername);
        //given(this.tiapService.getAccount(DEVICE_IOS, deviceUsername, devicePassword)).willReturn(testAcc);

        return template.postForEntity(
            base.toString() + "/oauth/token",
            getHttpEntity(deviceUsername, devicePassword),
            OAuth2AccessToken.class
        );
    }

    private HttpEntity<String> getHttpEntity(String username, String password) {
        final var body = "grant_type=password" +
            "&username=" + username +
            "&password=" + password;
        return new HttpEntity<>(body, getHttpHeaders());
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        String auth = "pXg4tlsLfOHX41CzR6Yxgk0h6YDHCKkG" + ":";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
        headers.set("Authorization", authHeader);
        return headers;
    }


    private static String refreshTokenKeyFor(String username) {
        return REFRESH_REDIS_SET_NAME + username;
    }

    private static String accessTokenKeyFor(String username) {
        return ACCESS_REDIS_SET_NAME + username;
    }

    private static String authKeyFor(String username) {
        return AUTH_REDIS_SET_NAME + username;
    }

    private static String refreshKeyFor(String username) {
        return REFRESH_REDIS_SET_NAME + username;
    }


}

