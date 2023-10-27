package org.geogebra.common.move.ggtapi.operations;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;

/**
 * Common interface for backend connectors.
 */
public interface BackendAPI {

	/**
	 * TODO get this from somewhere else, makes only sense for Tube API
	 *
	 * @return login API url
	 */
	String getLoginUrl();

	/**
	 * Set properties of user from JSON.
	 *
	 * @param user
	 *            user object
	 * @param loadLastUser
	 *            JSON encoded user data
	 * @return success
	 */
	boolean parseUserDataFromResponse(GeoGebraTubeUser user, String loadLastUser);

	/**
	 * Sends a request to the GeoGebraTube API to check if the login token which
	 * is defined in the specified GeoGebraTubeUser is valid.
	 *
	 * @param user
	 *            The user that should be authorized.
	 * @param logInOperation
	 *            login operation
	 * @param automatic
	 *            true if automatic
	 */
	void authorizeUser(GeoGebraTubeUser user, LogInOperation logInOperation, boolean automatic);

	/**
	 * @param clientInfo
	 *            client information
	 */
	void setClient(ClientInfo clientInfo);

	/**
	 * @return whether availability check was done
	 */
	boolean isCheckDone();

	/**
	 * @param lang
	 *            user language
	 * @param token
	 *            login token
	 */
	void setUserLanguage(String lang, String token);

	/**
	 * TODO only makes sense for Tube API, get it from somewhere else
	 *
	 * @return base URL
	 */
	String getUrl();

	/**
	 * Log user out.
	 *
	 * @param token
	 *            login token
	 */
	void logout(String token);

	/**
	 * Uploads a local saved file (web - localStorage; touch - device) to ggt
	 *
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	void uploadLocalMaterial(Material mat, MaterialCallbackI cb);

	/**
	 * Uploads the actual opened application to ggt
	 *
	 * @param tubeID - tube id
	 * @param visibility - visibility string
	 * @param filename - String
	 * @param base64 - base64 string
	 * @param cb - MaterialCallback
	 * @param type - material type
	 * @param isMultiuser - should set to multiuser shared
	 */
	void uploadMaterial(String tubeID, String visibility, String filename, String base64,
			MaterialCallbackI cb, MaterialType type, boolean isMultiuser);

	/**
	 * @param student
	 *            whether user is a student
	 * @return whether user can share files
	 */
	boolean canUserShare(boolean student);

	/**
	 * @return whether anonymous user can open shared links
	 */
	boolean anonymousOpen();

}
