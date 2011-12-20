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

public class Device extends ProductionNode 
{
	public Device(Context context, long nodeHandle, boolean addRef) throws StatusException 
	{
		super(context, nodeHandle, addRef);
	}
	
	public static Device create(Context context, Query query, EnumerationErrors errors) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateDevice(context.toNative(), handle,
			query == null ? 0 : query.toNative(),
			errors == null ? 0 : errors.toNative());
		WrapperUtils.throwOnError(status);
		Device result = (Device)context.createProductionNodeObject(handle.value, NodeType.DEVICE);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public static Device create(Context context, Query query) throws GeneralException
	{
		return create(context, query, null);
	}

	public static Device create(Context context) throws GeneralException
	{
		return create(context, null, null);
	}
	
	public DeviceIdentificationCapability getDeviceIdentificationCapability() throws StatusException
	{
		return new DeviceIdentificationCapability(this);
	}
}
