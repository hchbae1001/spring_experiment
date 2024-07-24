package bae.springexperiment.authtoken;

import bae.springexperiment.entity.AuthToken;
import bae.springexperiment.entity.enumerate.DeviceType;

import java.util.List;

public interface AuthTokenService {
    /**
     * This method retrieves the list of AuthTokens for the currently authenticated member using Spring Security.
     *
     * @return List of AuthTokens for the current member.
     */
    List<AuthToken> findAuthTokensByMemberId();

    /**
     * This method retrieves the list of AuthTokens for a specified member by member_id.
     *
     * @param member_id The ID of the member whose AuthTokens are to be retrieved.
     * @return List of AuthTokens for the specified member.
     */
    List<AuthToken> findAuthTokensByMemberId(Long member_id);

    /**
     * This method retrieves an AuthToken for the currently authenticated member and specified device type using Spring Security.
     *
     * @param deviceType The device type for which the AuthToken is to be retrieved.
     * @return The AuthToken for the current member and specified device type.
     */
    AuthToken findByMemberIdAndDeviceType(DeviceType deviceType);

    /**
     * This method retrieves an AuthToken for a specified member and device type.
     *
     * @param member_id The ID of the member whose AuthToken is to be retrieved.
     * @param deviceType The device type for which the AuthToken is to be retrieved.
     * @return The AuthToken for the specified member and device type.
     */
    AuthToken findByMemberIdAndDeviceType(Long member_id, DeviceType deviceType);

    /**
     * This method deletes all AuthTokens for a specified member by member_id.
     *
     * @param member_id The ID of the member whose AuthTokens are to be deleted.
     */
    void deleteAllByMemberId(Long member_id);

    /**
     * This method saves the given AuthToken.
     *
     * @param authToken The AuthToken to be saved.
     */
    void saveAuthToken(AuthToken authToken);

}
