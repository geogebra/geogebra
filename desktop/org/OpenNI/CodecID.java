/****************************************************************************
*                                                                           *
*  OpenNI 1.x Alpha                                                         *
*  Copyright (C) 2011 PrimeSense Ltd.                                       *
*                                                                           *
*  This file is part of OpenNI.                                             *
*                                                                           *
*  OpenNI is free software: you can redistribute it and/or modify           *
*  it under the terms of the GNU Lesser General Public License as published *
*  by the Free Software Foundation, either version 3 of the License, or     *
*  (at your option) any later version.                                      *
*                                                                           *
*  OpenNI is distributed in the hope that it will be useful,                *
*  but WITHOUT ANY WARRANTY; without even the implied warranty of           *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the             *
*  GNU Lesser General Public License for more details.                      *
*                                                                           *
*  You should have received a copy of the GNU Lesser General Public License *
*  along with OpenNI. If not, see <http://www.gnu.org/licenses/>.           *
*                                                                           *
****************************************************************************/
package org.OpenNI;

public class CodecID
{
	public static final CodecID Null = new CodecID((byte)0, (byte)0, (byte)0, (byte)0);
	public static final CodecID Uncompressed = new CodecID('N', 'O', 'N', 'E');
	public static final CodecID Jpeg = new CodecID('J', 'P', 'E', 'G');
	public static final CodecID Z16 = new CodecID('1', '6', 'z', 'P');
	public static final CodecID Z16WithTables = new CodecID('1', '6', 'z', 'T');
	public static final CodecID Z8 = new CodecID('I', 'm', '8', 'z');
	
	public CodecID(int nativeValue)
	{
		this.value = nativeValue;
	}
	
	public CodecID(byte byte1, byte byte2, byte byte3, byte byte4)
	{
		this(byte4 << 24 | byte3 << 16 | byte2 << 8 | byte1);
	}
	
	public CodecID(char char1, char char2, char char3, char char4)
	{
		this((byte)char1, (byte)char2, (byte)char3, (byte)char4);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.value;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodecID other = (CodecID) obj;
		if (this.value != other.value)
			return false;
		return true;
	}

	public int toNative() { return this.value; }
	
	private int value;
}
