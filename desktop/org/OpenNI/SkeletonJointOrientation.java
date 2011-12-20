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

public class SkeletonJointOrientation
{
	public SkeletonJointOrientation(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float confidence)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.x3 = x3;
		this.y3 = y3;
		this.z3 = z3;
		this.confidence = confidence;
	}
	
	public float getX1()
	{
		return this.x1;
	}
	public float getY1()
	{
		return this.y1;
	}
	public float getZ1()
	{
		return this.z1;
	}
	public float getX2()
	{
		return this.x2;
	}
	public float getY2()
	{
		return this.y2;
	}
	public float getZ2()
	{
		return this.z2;
	}
	public float getX3()
	{
		return this.x3;
	}
	public float getY3()
	{
		return this.y3;
	}
	public float getZ3()
	{
		return this.z3;
	}
	public float getConfidence()
	{
		return this.confidence;
	}

	private float x1;
	private float y1;
	private float z1;
	private float x2;
	private float y2;
	private float z2;
	private float x3;
	private float y3;
	private float z3;
	private float confidence;
}
