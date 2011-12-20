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

public class SkeletonJointTransformation
{
	public SkeletonJointTransformation(SkeletonJointPosition position, SkeletonJointOrientation orientation)
	{
		this.position = position;
		this.orientation = orientation;
	}
	
	public SkeletonJointPosition getPosition()
	{
		return position;
	}
	public SkeletonJointOrientation getOrientation()
	{
		return orientation;
	}
	
	private SkeletonJointPosition position;
	private SkeletonJointOrientation orientation;

}
