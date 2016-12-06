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

import java.util.EventListener;

/**
 * Classes that implement PatternListener and add themselves as listeners to a
 * <code>Pattern</code> object will receive events when new fragments are added
 * to a <code>Pattern</code>. This is mostly intended to be used by the
 * <code>Player</code> for handling streaming music.
 * 
 * @see Pattern
 * @see Player
 *
 * @author David Koelle
 * @version 3.0
 */
public interface PatternListener extends EventListener {
	/**
	 * Called when a new fragment has been added to a pattern
	 * 
	 * @param pattern
	 *            the fragment that has been added
	 */
	public void fragmentAdded(Pattern fragment);
}
