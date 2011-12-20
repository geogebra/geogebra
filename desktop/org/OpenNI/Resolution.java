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

import java.util.NoSuchElementException;

public enum Resolution 
{
	CUSTOM (0),
	QQVGA (1),
	CGA (2),
	QVGA (3),
	VGA (4),
	SVGA (5),
	XGA (6),
	P720 (7),
	SXGA (8),
	UXGA (9),
	P1080 (10);
	
	Resolution(int val)
	{
		this.val = val;
		this.xRes = NativeMethods.xnResolutionGetXRes(val);
		this.yRes = NativeMethods.xnResolutionGetYRes(val);
		this.name = NativeMethods.xnResolutionGetName(val);
	}
	
	public int getxRes()
	{
		return this.xRes;
	}

	public int getyRes()
	{
		return this.yRes;
	}

	public String getName()
	{
		return this.name;
	}
	
	public int toNative() { return this.val; }
	
	public static Resolution fromNative(int value)
	{
		for (Resolution type : Resolution.values()) 
		{
			if (type.val == value)
				return type;
		}
		
		throw new NoSuchElementException();
	}
	
	public static Resolution fromName(String name)
	{
		return fromNative(NativeMethods.xnResolutionGetFromName(name));
	}
	
	public static Resolution fromXYRes(int xRes, int yRes)
	{
		return fromNative(NativeMethods.xnResolutionGetFromXYRes(xRes, yRes));
	}
	
	private final int val;
	private final int xRes;
	private final int yRes;
	private final String name;
}
