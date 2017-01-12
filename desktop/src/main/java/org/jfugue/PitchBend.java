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
 * Represents pitch bend changes.
 *
 * @author David Koelle
 * @version 3.0
 */
public final class PitchBend implements JFugueElement {
	private byte lsb;
	private byte msb;

	/**
	 * Creates a new Pitch Bend object, with the specified tempo value. Integer
	 * value = msb * 0x80 + lsb (0x80 hex == 128 dec)
	 * 
	 * @param lsb
	 *            the least significant byte for the pitch bend for this object
	 * @param msb
	 *            the most significant byte for the pitch bend for this object
	 */
	public PitchBend(byte lsb, byte msb) {
		setPitchBend(lsb, msb);
	}

	/**
	 * Sets the value of the pitch bend for this object.
	 * 
	 * @param tempo
	 *            the pitch bend for this object
	 */
	public void setPitchBend(byte lsb, byte msb) {
		this.lsb = lsb;
		this.msb = msb;
	}

	/**
	 * Returns the value of the pitch bend for this object.
	 * 
	 * @return the value of the pitch bend for this object
	 */
	public byte[] getBend() {
		return new byte[] { lsb, msb };
	}

	/**
	 * Returns the Music String representing this element and all of its
	 * settings. For a PitchBend object, the Music String is <code>&</code>
	 * <i>int</i> or <code>&</code><i>lsb,msb</i>
	 * 
	 * @return the Music String for this element
	 */
	@Override
	public String getMusicString() {
		StringBuffer buffy = new StringBuffer();
		buffy.append("&");
		buffy.append(getBend()[1] * 0x80 + getBend()[0]);
		return buffy.toString();
	}

	/**
	 * Returns verification string in this format: PitchBend: bend={#}
	 * 
	 * @version 4.0
	 */
	@Override
	public String getVerifyString() {
		StringBuffer buffy = new StringBuffer();
		buffy.append("PitchBend: bend={");
		buffy.append(lsb);
		buffy.append(',');
		buffy.append(msb);
		buffy.append('}');
		return buffy.toString();
	}

}