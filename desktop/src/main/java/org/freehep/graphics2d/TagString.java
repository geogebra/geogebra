// Copyright 2000, CERN, Geneva, Switzerland and University of Santa Cruz, California, U.S.A.
package org.freehep.graphics2d;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: TagString.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class TagString {

	private String string;

	public TagString(String value) {
		string = value;
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	@Override
	final public boolean equals(Object obj) {
		if (obj == null || string == null) {
			return false;
		}
		return string.equals(obj);
	}

	@Override
	public String toString() {
		return string;
	}
}
