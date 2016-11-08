/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * MyError.java
 *
 * Created on 04. Oktober 2001, 09:29
 */

package org.geogebra.common.main;

/**
 * 
 * @author Markus
 */
public class MyError extends java.lang.Error {

	private static final long serialVersionUID = 1L;
	/** application */
	protected Localization loc;
	private String[] strs;
	private String commandName = null;

	/**
	 * Creates new MyError
	 * 
	 * @param loc0
	 *            application
	 * @param errorName
	 *            error name (should be a key in error.properties)
	 */
	public MyError(Localization loc0, String errorName) {
		// set localized message
		super(errorName);
		this.loc = loc0;
	}

	/**
	 * @param app
	 *            application
	 * @param errorName
	 *            error name (should be a key in error.properties)
	 * @param commandName
	 *            associated command name
	 */
	public MyError(Localization app, String errorName, String commandName, Throwable cause) {
		// set localized message
		super(errorName, cause);
		this.loc = app;
		this.commandName = commandName;
	}

	/**
	 * @param app
	 *            application
	 * @param strs
	 *            lines of the error
	 */
	public MyError(Localization app, String[] strs) {
		this.loc = app;
		// set localized message
		this.strs = strs;
	}

	/**
	 * @return associated command name
	 */
	public String getcommandName() {
		return commandName;
	}

	@Override
	public String getLocalizedMessage() {
		if (strs == null) {
			return loc.getError(getMessage());
		}
		StringBuilder sb = new StringBuilder();
		// space needed in case error is displayed on one line
		sb.append(loc.getError(strs[0]) + " \n");
		for (int i = 1; i < strs.length; i++) {
			sb.append(loc.getError(strs[i]) + " ");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getClass().toString() + ": ");
		if (strs == null)
			sb.append(loc.getError(getMessage()));
		else {
			for (int i = 0; i < strs.length; i++) {
				sb.append(loc.getError(strs[i]) + " : ");
			}
		}
		return sb.toString();
	}

}
