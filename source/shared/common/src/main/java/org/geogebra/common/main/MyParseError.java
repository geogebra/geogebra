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
 * Created on 04. October 2001, 09:29
 */

package org.geogebra.common.main;

/**
 *
 * @author Markus
 * 
 */
public class MyParseError extends MyError {

	private static final long serialVersionUID = 1L;

	/**
	 * @param loc
	 *            localization
	 * @param message
	 *            primary message (translation key)
	 * @param strs
	 *            parts of the error (space separated)
	 */
	public MyParseError(Localization loc, Errors message, String... strs) {
		super(loc, message, strs);
	}

	@Override
	public String getLocalizedMessage() {
		return Errors.InvalidInput.getError(loc)
				+ " :\n"
				+ super.getLocalizedMessage();
	}
}
