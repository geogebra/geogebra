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
 * Represents a measure marker.  This has no bearing on the audio produced,
 * but is useful for making music strings more readable, and for listening
 * to progress as a song is played.
 *
 *@author David Koelle
 *@version 3.0
 */
public final class Measure implements JFugueElement
{
    /**
     * Creates a new Measure object, which is simply an indicator
     * that a measure line has been parsed in a MusicString
     */
    public Measure()
    {
    }

    /**
     * Returns the Music String representing this element.
     * For a Measure object, the Music String is <code>|</code>
     * @return the Music String for this element
     */
    public String getMusicString()
    {
        return "|";
    }

    /**
     * Returns verification string in this format:
     * Measure
     * @version 4.0
     */
    public String getVerifyString()
    {
        return "Measure";
    }
}