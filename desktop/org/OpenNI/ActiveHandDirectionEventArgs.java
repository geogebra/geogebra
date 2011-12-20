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

public class ActiveHandDirectionEventArgs extends EventArgs
{
	public ActiveHandDirectionEventArgs(int id, Point3D position, float time, Direction direction)
	{
		this.id = id;
		this.position = position;
		this.time = time;
		this.direction = direction;
	}
	
	public int getId()
	{
		return this.id;
	}
	public Point3D getPosition()
	{
		return this.position;
	}
	public float getTime()
	{
		return this.time;
	}
	public Direction getDirection()
	{
		return this.direction;
	}
	
	
	private final int id;
	private final Point3D position;
	private final float time;
	private final Direction direction;

}
