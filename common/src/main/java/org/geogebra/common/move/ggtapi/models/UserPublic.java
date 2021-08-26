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
	private String username;
	private int id;
	private String displayname;

	/**
	 * public user information
	 */
	public UserPublic() {
		this.username = "";
		this.id = -1;
		this.displayname = "";
	}

	/**
	 * Public user information
	 * 
	 * @param username
	 *            The username
	 * @param id
	 *            Unique user ID
	 * @param displayname
	 *            The real name if specified, same as username otherwise
	 */
	public UserPublic(String username, int id, String displayname) {
		this.username = username;
		this.id = id;
		this.displayname = displayname;
	}

	/**
	 * Get username
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set user name
	 * 
	 * @param username
	 *            the username
	 */
	public void setUsername(String username) {
		this.username = username;
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
	public String getDisplayname() {
		return displayname;
	}

	/**
	 * Set users display name
	 * 
	 * @param displayname
	 *            The real name if specified, same as username otherwise
	 */
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
}
