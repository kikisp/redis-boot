package redis.spike.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CustomRedisTokenStore extends RedisTokenStore {

    private static final String UNAME_TO_REFRESH = "uname_to_refresh:";
    private static final String UNAME_TO_CLIENT = "uname_to_clientId:";



    @Autowired
    RedisTemplate redisTemplate;
    // TOOD create MarketAuthenticationKeyGenerator
    private DefaultAuthenticationKeyGenerator authenticationKeyGenerator;

    public CustomRedisTokenStore(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        super.storeRefreshToken(refreshToken,authentication);
        // TODO change to set
        redisTemplate.opsForSet().add(UNAME_TO_REFRESH.concat(getRefreshTokenApprovalKey(authentication)),refreshToken);
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken)refreshToken;
            Date expiration = expiringRefreshToken.getExpiration();
            if (expiration != null) {
                int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L).intValue();
                redisTemplate.expire(UNAME_TO_REFRESH.concat(getRefreshTokenApprovalKey(authentication)),seconds, TimeUnit.SECONDS);


            }
        }

    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        super.storeAccessToken(token,authentication);
        redisTemplate.opsForSet().add(UNAME_TO_CLIENT.concat(authentication.getName()),authentication.getOAuth2Request().getClientId());
        redisTemplate.expire(UNAME_TO_CLIENT.concat(authentication.getName()),token.getExpiresIn(),TimeUnit.SECONDS);
    }


    public void removeRefreshToken(Collection<String> usernames) {
        for (String username:usernames) {
            Set<OAuth2RefreshToken> refreshTokenList = redisTemplate.opsForSet().members(UNAME_TO_REFRESH.concat(username));
            for (OAuth2RefreshToken refreshToken:refreshTokenList) {
               super.removeRefreshToken(refreshToken);
            }
            redisTemplate.delete(UNAME_TO_REFRESH.concat(username));
        }
    }

    public void removeAccessToken(Collection<String> usernames) {
        for (String username:usernames) {
            Set<String> clientList = redisTemplate.opsForSet().members(UNAME_TO_CLIENT.concat(username));
            for (String client:clientList) {
               Collection<OAuth2AccessToken> tokens = super.findTokensByClientIdAndUserName(client,username);
                for (OAuth2AccessToken token:tokens) {
                    super.removeAccessToken(token);
                }
            }
            redisTemplate.delete(UNAME_TO_CLIENT.concat(username));
        }

    }

    public Collection<OAuth2AccessToken> getTokensByUsername(String username) {
        Set<String> clientList = redisTemplate.opsForSet().members(UNAME_TO_CLIENT.concat(username));
        Collection<OAuth2AccessToken> tokenCollection = new HashSet<>();
        for (String client : clientList) {
            tokenCollection.addAll(super.findTokensByClientIdAndUserName(client, username));
        }
        return tokenCollection;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        OAuth2AccessToken token =(OAuth2AccessToken) redisTemplate.opsForValue().get("auth_to_access:" + key);
        return token;

    }

    private static String getRefreshTokenApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? "" : authentication.getUserAuthentication().getName();
        return userName;
    }





}
