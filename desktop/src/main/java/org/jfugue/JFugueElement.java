/*
 * JFugue - API for Music Programming
 * Copyright (C) 2003-2008  David Koelle
 *
 * http://www.jfugue.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.jfugue;

/**
 * This is the base class for the JFugue elements, including Voice, Instrument,
 * Note, Controller, and Tempo. It requires that elements be able to return a
 * Music String representation of their settings.
 *
 * @author David Koelle
 * @version 2.0
 * @version 4.0 - Added getVerifyString()
 * @version 4.0.3 - Now extends Serializable
 */
public interface JFugueElement {
	/**
	 * Returns the Music String representing this element and all of its
	 * settings.
	 * 
	 * @return the Music String for this element
	 */
	public String getMusicString();

	/**
	 * Returns a verification string, which should contain a String
	 * representation of all of the aspects of the given element. This should be
	 * in the following form: Thing: key=value, key=value, key=value,... For
	 * example: Note: value=60, duration=0.25
	 *
	 * @version 4.0
	 */
	public String getVerifyString();
}
