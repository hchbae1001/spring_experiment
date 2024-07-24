package bae.springexperiment.authtoken;

import bae.springexperiment.entity.AuthToken;
import bae.springexperiment.entity.enumerate.DeviceType;

import java.util.List;

public interface AuthTokenService {

    /**
     * This method retrieves the list of AuthTokens for a specified member by member_id.
     *
     * @param member_id The ID of the member whose AuthTokens are to be retrieved.
     * @return List of AuthTokens for the specified member.
     */
    List<AuthToken> findByMemberId(Long member_id);

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
    void save(AuthToken authToken);


    /**
     * Updates the refresh token for a specific member and device type.
     * This method is used during refresh token rotation to apply the newly issued refresh token.
     *
     * @param member_id The ID of the member.
     * @param deviceType The type of device.
     * @param refreshToken The new refresh token.
     */
    void updateRefreshToken(Long member_id, DeviceType deviceType, String refreshToken);

    /**
     * When a member attempts to log out of the application, call this method.
     * This will erase the authentication token information for the specified member and device type.
     *
     * @param member_id The ID of the member.
     * @param deviceType The type of device.
     */
    void deleteByMemberIdAndDeviceType(Long member_id, DeviceType deviceType);

}
