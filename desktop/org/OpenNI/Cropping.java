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

public class Cropping 
{
	public Cropping(int xOffset, int yOffset, int xSize, int ySize, boolean enabled) 
	{
		super();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xSize = xSize;
		this.ySize = ySize;
		this.enabled = enabled;
	}

	public int getXOffset() 
	{
		return xOffset;
	}
	public void setXOffset(int xOffset) 
	{
		this.xOffset = xOffset;
	}
	public int getYOffset() 
	{
		return yOffset;
	}
	public void setYOffset(int yOffset) 
	{
		this.yOffset = yOffset;
	}
	public int getXSize() 
	{
		return xSize;
	}
	public void setXSize(int xSize) 
	{
		this.xSize = xSize;
	}
	public int getYSize() 
	{
		return ySize;
	}
	public void setYSize(int ySize) 
	{
		this.ySize = ySize;
	}
	public boolean isEnabled() 
	{
		return enabled;
	}
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}
	
	private int xOffset;
	private int yOffset;
	private int xSize;
	private int ySize;
	private boolean enabled;
}
