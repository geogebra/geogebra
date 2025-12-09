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

package org.geogebra.common.move.ggtapi.models;

import java.io.Serializable;

/**
 * MarvlAPI supplies creator of material
 * 
 * @author Alicia
 *
 */
public class UserPublic implements Serializable {
	/**
	 * Serialization needed for Android.
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String displayName;

	/**
	 * public user information
	 */
	public UserPublic() {
		this.id = -1;
		this.displayName = "";
	}

	/**
	 * Public user information
	 * 
	 * @param id
	 *            Unique user ID
	 * @param displayName
	 *            The real name if specified, same as username otherwise
	 */
	public UserPublic(int id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	/**
	 * Get user ID
	 * 
	 * @return Unique key for identification of user
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set user ID
	 * 
	 * @param id
	 *            Unique key for identification of user
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get users display name
	 * 
	 * @return The real name if specified, same as username otherwise
	 */
	public String getDisplayName() {
		return displayName;
	}

}
