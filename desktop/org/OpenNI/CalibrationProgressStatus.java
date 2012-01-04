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

public enum CalibrationProgressStatus
{
	OK(0),
	NO_USER(1),
	ARM(2),
	LEG(3),
	HEAD(4),
	TORSO(5),
	TOP_FOV(6),
	SIDE_FOV(7),
	POSE(8),
	MANUAL_ABORT(9),
	MANUAL_RESET(10),
	TIMEOUT_FAIL(11);

	
	CalibrationProgressStatus(int val)
	{
		this.val = val;
	}
	
	public int toNative() { return this.val; }
	
	public static CalibrationProgressStatus fromNative(int value)
	{
		for (CalibrationProgressStatus type : CalibrationProgressStatus.values()) 
		{
			if (type.val == value)
				return type;
		}
		
		throw new NoSuchElementException();
	}
	
	private final int val;



}
