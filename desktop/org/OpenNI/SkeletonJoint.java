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

public enum SkeletonJoint
{
	HEAD(1),
	NECK(2),
	TORSO(3),
	WAIST(4),
	LEFT_COLLAR(5),
	LEFT_SHOULDER(6),
	LEFT_ELBOW(7),
	LEFT_WRIST(8),
	LEFT_HAND(9),
	LEFT_FINGER_TIP(10),
	RIGHT_COLLAR(11),
	RIGHT_SHOULDER(12),
	RIGHT_ELBOW(13),
	RIGHT_WRIST(14),
	RIGHT_HAND(15),
	RIGHT_FINGER_TIP(16),
	LEFT_HIP(17),
	LEFT_KNEE(18),
	LEFT_ANKLE(19),
	LEFT_FOOT(20),
	RIGHT_HIP(21),
	RIGHT_KNEE(22),
	RIGHT_ANKLE(23),
	RIGHT_FOOT(24);

	SkeletonJoint(int val)
	{
		this.val = val;
	}
	
	public int toNative() { return this.val; }
	
	public static SkeletonJoint fromNative(int value)
	{
		for (SkeletonJoint type : SkeletonJoint.values()) 
		{
			if (type.val == value)
				return type;
		}
		
		throw new NoSuchElementException();
	}
	
	private final int val;
}
