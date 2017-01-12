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
 * This Adapter class implements all of the methods of PatternListener, but the
 * implementations are blank. If you want something to be a PatternListener, but
 * you don't want to implement all of the PatternListener methods, extend this
 * class.
 * 
 * @author David Koelle
 * @version 3.0
 */
public class PatternListenerAdapter implements PatternListener {
	/**
	 * Called when a new fragment has been added to a pattern
	 * 
	 * @param pattern
	 *            the fragment that has been added
	 */
	@Override
	public void fragmentAdded(Pattern fragment) {
	}
}
