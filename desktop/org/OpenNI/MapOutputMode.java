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

public class MapOutputMode 
{
	public MapOutputMode(int xRes, int yRes, int FPS)
	{
		this.xRes = xRes;
		this.yRes = yRes;
		this.FPS = FPS;
	}
	
	public void setXRes(int xRes)
	{
		this.xRes = xRes;
	}

	public void setYRes(int yRes)
	{
		this.yRes = yRes;
	}

	public void setFPS(int fPS)
	{
		this.FPS = fPS;
	}

	public int getXRes() { return this.xRes; }
	public int getYRes() { return this.yRes; }
	public int getFPS() { return this.FPS; }
	
	private int xRes;
	private int yRes;
	private int FPS;
}
