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

import java.nio.ShortBuffer;

public class ShortMap extends Map 
{
	ShortMap()
	{
		super();
		this.setBytesPerPixel(BYTES_PER_PIXEL);
	}
	
	ShortMap(long ptr, int xRes, int yRes)
	{
		super(ptr, xRes, yRes, BYTES_PER_PIXEL);
	}
	
	public short readPixel(int x, int y)
	{
		return NativeMethods.readShort(getPixelPtr(x, y));
	}
	
	public ShortBuffer createShortBuffer()
	{
		return createByteBuffer().asShortBuffer();
	}
	
	private static final int BYTES_PER_PIXEL = Short.SIZE / 8;
}
