package com.incept5.rest.user.service;

import com.incept5.rest.user.api.CreateUserRequest;
import com.incept5.rest.user.api.LoginRequest;
import com.incept5.rest.user.api.UpdateUserRequest;
import com.incept5.rest.user.domain.Role;
import com.incept5.rest.user.api.ExternalUser;
import org.springframework.social.connect.Connection;

/**
 * @author: Iain Porter
 *
 * Service to manage users
 */
public interface UserService {


    /**
     * Create a new User with the given role
     *
     * @param request
     * @param role
     * @return User
     */
    public ExternalUser createUser(CreateUserRequest request, Role role);


    /**
     * Create a Default User with a given role
     *
     * @param role
     * @return User
     */
    public ExternalUser createUser(Role role);

    /**
     * Login a User
     *
     * @param request
     * @return AuthenticatedUserToken
     */
    public ExternalUser login(LoginRequest request);

    /**
     * Log in a User using Connection details from an authorized request from the User's supported Social provider
     * encapsulated in the {@link org.springframework.social.connect.Connection} parameter
     *
     * @param connection containing the details of the authorized user account form the Social provider
     * @return the User account linked to the {@link com.incept5.rest.user.domain.SocialUser} account
     */
    public ExternalUser socialLogin(Connection<?> connection);

    /**
     * Get a User based on a unique identifier
     *
     * Identifiers supported are uuid, emailAddress
     *
     * @param userIdentifier
     * @return  User
     */
    public ExternalUser getUser(ExternalUser requestingUser, String userIdentifier);

    /**
     * Delete user, only authenticated user accounts can be deleted
     *
     * @param userMakingRequest the user authorized to delete the user
     * @param userId the id of the user to delete
     */
    public void deleteUser(ExternalUser userMakingRequest, String userId);

    /**
     * Save User
     *
     * @param userId
     * @param request
     */
    public ExternalUser saveUser(String userId, UpdateUserRequest request);

    /**
     * Save the active session state of the externalUser
     * @param user
     */
    public void saveUserSession(ExternalUser user);

    /**
     * Delete all SessionToken objects that have not been accessed within the duration specified by the argument timeSinceLastUpdatedInMinutes
     *
     * @param timeSinceLastUpdatedInMinutes
     * @return the number of sessions removed
     */
    public Integer deleteExpiredSessions(int timeSinceLastUpdatedInMinutes);

}
