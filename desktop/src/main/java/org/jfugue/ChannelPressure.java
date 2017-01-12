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
 * Represents channel pressure changes.
 *
 * @author David Koelle
 * @version 3.0
 */
public final class ChannelPressure implements JFugueElement {
	private byte pressure;

	/**
	 * Creates a new channel pressure object, with the specified key and
	 * pressure values.
	 * 
	 * @param key
	 *            the key to apply pressure to
	 * @param pressure
	 *            the pressure to apply
	 */
	public ChannelPressure(byte pressure) {
		setPressure(pressure);
	}

	/**
	 * Sets the pressure value of this object.
	 * 
	 * @param pressure
	 *            the pressure for this object
	 */
	public void setPressure(byte pressure) {
		this.pressure = pressure;
	}

	/**
	 * Returns the pressure for this object.
	 * 
	 * @return the pressure for this object
	 */
	public byte getPressure() {
		return this.pressure;
	}

	/**
	 * Returns the Music String representing this element and all of its
	 * settings. For a channel pressure object, the Music String is
	 * <code>+</code><i>key,pressure</i>
	 * 
	 * @return the Music String for this element
	 */
	@Override
	public String getMusicString() {
		StringBuffer buffy = new StringBuffer();
		buffy.append("+");
		buffy.append(getPressure());
		return buffy.toString();
	}

	/**
	 * Returns verification string in this format: ChannelPressure: pressure={#}
	 * 
	 * @version 4.0
	 */
	@Override
	public String getVerifyString() {
		StringBuffer buffy = new StringBuffer();
		buffy.append("ChannelPressure: pressure=");
		buffy.append(getPressure());
		return buffy.toString();
	}

}