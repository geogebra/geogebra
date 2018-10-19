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
	 *            localization
	 * @param errorName
	 *            error name (should be a key in error.properties)
	 */
	public MyError(Localization loc0, String errorName) {
		// set localized message
		super(errorName);
		this.loc = loc0;
	}

	/**
	 * @param loc0
	 *            localization
	 * @param errorName
	 *            error name (should be a key in error.properties)
	 * @param commandName
	 *            associated command name
	 * @param cause
	 *            cause
	 * @return command error
	 */
	public static MyError forCommand(Localization loc0, String errorName,
			String commandName,
			Throwable cause) {
		// set localized message
		MyError ret = new MyError(errorName, cause);
		ret.loc = loc0;
		ret.commandName = commandName;
		return ret;
	}

	/**
	 * @param loc0
	 *            localization
	 * @param message
	 *            primary message
	 * @param strs
	 *            parts of the error (space separated)
	 */
	public MyError(Localization loc0, String message, String... strs) {
		super(message);
		this.loc = loc0;
		// set localized message
		this.strs = strs;
	}

	/**
	 * @param errorName
	 *            translateable error name
	 * @param cause
	 *            cause
	 */
	public MyError(String errorName, Throwable cause) {
		super(errorName, cause);
	}

	/**
	 * @return associated command name
	 */
	public String getcommandName() {
		return commandName;
	}

	@Override
	public String getLocalizedMessage() {
		StringBuilder sb = new StringBuilder();
		// space needed in case error is displayed on one line
		sb.append(getError(getMessage()));
		if(strs != null){
			sb.append(" \n");
			for (String part : strs) {
				sb.append(part);
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getClass().toString());
		sb.append(": ");
		sb.append(getError(getMessage()));
		if (strs != null) {
			for (int i = 0; i < strs.length; i++) {
				sb.append(getError(strs[i]));
				sb.append(" : ");
			}
		}
		return sb.toString();
	}

	/**
	 * @param s
	 *            key
	 * @return localized error
	 */
	protected String getError(String s) {
		String ret = loc == null ? s : loc.getError(s);

		// no loc, or running webSimple
		if ("UndefinedVariable".equals(ret)) {
			return "Undefined variable";
		}
		if ("ReplaceFailed".equals(ret)) {
			return "Redefinition failed";
		}
		if("InvalidInput".equals(ret)){
			return "Please check your input";
		}

		return ret;
	}

}
