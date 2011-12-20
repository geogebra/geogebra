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

public class BoundingBox3D
{
	public BoundingBox3D(Point3D leftBottomNear, Point3D rightTopFar)
	{
		this.leftBottomNear = leftBottomNear;
		this.rightTopFar = rightTopFar;
	}

	public Point3D getLeftBottomNear()
	{
		return this.leftBottomNear;
	}
	public Point3D getRightTopFar()
	{
		return this.rightTopFar;
	}
	
	public Point3D getMins()
	{
		return getLeftBottomNear();
	}
	public Point3D getMaxs()
	{
		return getRightTopFar();
	}
	
	private final Point3D leftBottomNear;
	private final Point3D rightTopFar;
}
