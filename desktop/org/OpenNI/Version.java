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

public class Version 
{
	public Version(byte major, byte minor, short maintenance, int build)
	{
		this.major = major;
		this.minor = minor;
		this.maintenance = maintenance;
		this.build = build;
	}

	public byte getMajor()
	{
		return this.major;
	}
	public byte getMinor()
	{
		return this.minor;
	}
	public short getMaintenance()
	{
		return this.maintenance;
	}
	public int getBuild()
	{
		return this.build;
	}
	
	private final byte major;
	private final byte minor;
	private final short maintenance;
	private final int build;
}
