package com.incept5.rest.authorization;

import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.user.domain.User;
import com.incept5.rest.user.repository.UserRepository;
import com.incept5.rest.user.service.UserService;
import com.incept5.rest.util.DateUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: porter
 * Date: 15/03/2012
 * Time: 11:41
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class AuthorizationServiceTest {

    private static final String SESSION_TOKEN = "123456789-abcd-efg-hijk";

    private static final User USER = new User();

    @Autowired
    private AuthorizationService authorziationService;

    @Configuration
    static class ContextConfiguration {

        @Bean
        public UserRepository userRepository() {
            UserRepository repo = mock(UserRepository.class);
            when(repo.findByUuid(eq(USER.getUuid().toString()))).thenReturn(USER);
            return repo;
        }

        @Bean
        public AuthorizationService authorizationService() {
            AuthorizationService svc = new AuthorizationService(mock(UserService.class));
            return svc;
        }

    }

    @Test
    public void authorizeUser() throws Exception {
        String dateString = DateUtil.getCurrentDateAsIso8061String();
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(USER.getSessions().first().getToken() + ":hash123,123,POST," + dateString + ",123")));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "hash123,123", dateString, "123"));
        assertThat(isAuthorized, is(true));
    }

    @Test
    public void invalidUnencodedRequest() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(SESSION_TOKEN + ":hash123,123")));
        User user = new User();
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "hash123,1234", "123"));
        assertThat(isAuthorized, is(false));
    }

    @Test
    public void invalidSessionToken() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256("INVALID-SESSION-TOKEN:abcdef")));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "abcdef", "123"));
        assertThat(isAuthorized, is(false));
    }

    @Test
    public void missingNonce() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256("INVALID-SESSION-TOKEN:abcdef")));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "abcdef", null));
        assertThat(isAuthorized, is(false));
    }

    @Test
    public void wrongNonce() {
        String dateString = DateUtil.getCurrentDateAsIso8061String();
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(USER.getSessions().first().getToken() + ":hash123,123,POST," + dateString + ",123")));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "hash123,123", dateString, "567"));
        assertThat(isAuthorized, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUser() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(SESSION_TOKEN + ":abcdef")));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(null, hashedToken,  "abcdef", "123"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSessionToken() {
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), null,  "abcdef", "123"));
    }

    private AuthorizationRequest getAuthorizationRequest(ExternalUser user, String hashedToken, String requestString, String nonce) {
        return getAuthorizationRequest(user, hashedToken, requestString, DateUtil.getCurrentDateAsIso8061String(), nonce);
    }

    private AuthorizationRequest getAuthorizationRequest(ExternalUser user, String hashedToken, String requestString, String dateString, String nonce) {
        AuthorizationRequest authRequest = new AuthorizationRequest(user, requestString, "POST", dateString,
                hashedToken, nonce);
        return authRequest;
    }
}
