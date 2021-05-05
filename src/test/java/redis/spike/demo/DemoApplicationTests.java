package redis.spike.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import redis.clients.jedis.Jedis;
import redis.spike.demo.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestServiceConfiguration.class})
class DemoApplicationTests {




    public static final String REFRESH_REDIS_SET_NAME = "refresh:";
    public static final String ACCESS_REDIS_SET_NAME = "access:";
    public static final String AUTH_REDIS_SET_NAME = "auth:";
    public static final String USERNAME = "stefan";
    public static final String PASSWORD = "admin";
    public static final String CLIENT_ID = "webapp";
    public static final String CLIENT_PASS = "root";



    private URL base;

    @Autowired
    private TestRestTemplate template;


    @Autowired
    private Jedis jedis;

    @BeforeEach
    public void setUp() throws Exception {
        jedis.flushDB();
        base = new URL("http://localhost:" + 8080);
    }


    /////

    @Test
    public void creatingAValidDeviceTokenReturns200WithTokenPayload() {
        final var response = requestDeviceToken();
        final var oAuth2AccessToken = response.getBody();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(oAuth2AccessToken);
    }


    @Test
    public void requestingAnAuthTokenFromARefreshTokenReturnsANewDeviceToken() {
        final var deviceToken = createDeviceToken();
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

    private OAuth2AccessToken createDeviceToken() {
        return requestDeviceToken().getBody();
    }

    private ResponseEntity<OAuth2AccessToken> requestDeviceToken() {


        return template.postForEntity(
            base.toString() + "/oauth/token",
            getHttpEntity(),
            OAuth2AccessToken.class
        );
    }

    private HttpEntity<String> getHttpEntity() {
        final var body = "grant_type=password" +
            "&username=" + DemoApplicationTests.USERNAME +
            "&password=" + DemoApplicationTests.PASSWORD;
        return new HttpEntity<>(body, getHttpHeaders());
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        String auth = DemoApplicationTests.CLIENT_ID + ":" + DemoApplicationTests.CLIENT_PASS;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
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

