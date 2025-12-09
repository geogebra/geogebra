/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.move.ggtapi.events;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;

/**
 * Event for login operations
 * 
 * @author gabor
 *
 */
public class LoginEvent extends BaseEvent {
	private GeoGebraTubeUser user;
	private boolean successful;
	private boolean automatic;
	private String userJSON;

	/**
	 * Creates a new Login event,
	 * 
	 * @param user
	 *            The user that was logged in
	 * @param successful
	 *            If the Login was successful
	 * @param automatic
	 *            true if the login was performed automatically (on startup) or
	 *            manually by the user
	 * @param userJSON
	 *            JSON with user data
	 */
	public LoginEvent(GeoGebraTubeUser user, boolean successful,
			boolean automatic, String userJSON) {
		super("login" + successful + "," + automatic);
		this.user = user;
		this.successful = successful;
		this.automatic = automatic;
		this.userJSON = userJSON;
	}

	/**
	 * @return if the login attempt was successful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @return if the login attempt was successful
	 */
	public boolean isAutomatic() {
		return automatic;
	}

	/**
	 * @return the logged in user including all user information
	 */
	public GeoGebraTubeUser getUser() {
		return user;
	}

	/**
	 * @return JSON with user data
	 */
	public String getJSON() {
		return this.userJSON;
	}
}
