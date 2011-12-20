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

public class WrapperUtils
{
	public static void throwOnError(int status) throws StatusException
	{
		if (status != 0)
		{
			throw new StatusException(status);
		}
	}
	
	public static void checkEnumeration(int status, EnumerationErrors errors) throws GeneralException
	{
		if (status != 0)
		{
			if (errors != null && !errors.isEmpty())
			{
				throw new GeneralException(errors.toString());
			}
			else
			{
				throw new StatusException(status);
			}
		}
	}
	
	public static String getErrorMessage(int status)
	{
		String message = NativeMethods.xnGetStatusString(status);
		return message;
	}
}
