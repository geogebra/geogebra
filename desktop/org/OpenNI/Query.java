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

public class Query extends ObjectWrapper
{
	public Query() throws GeneralException 
	{
		super(allocate());
	}
	
	public void setVendor(String vendor) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetVendor(toNative(), vendor);
		WrapperUtils.throwOnError(status);
	}

	public void setName(String name) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetName(toNative(), name);
		WrapperUtils.throwOnError(status);
	}

	public void setMinVersion(Version version) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetMinVersion(toNative(), version.getMajor(), version.getMinor(), version.getMaintenance(), version.getBuild());
		WrapperUtils.throwOnError(status);
	}

	public void setMaxVersion(Version version) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetMaxVersion(toNative(), version.getMajor(), version.getMinor(), version.getMaintenance(), version.getBuild());
		WrapperUtils.throwOnError(status);
	}

	public void addSupportedCapability(Capability capability) throws StatusException
	{
		int status = NativeMethods.xnNodeQueryAddSupportedCapability(toNative(), capability.getName());
		WrapperUtils.throwOnError(status);
	}

	public void addSupportedMapOutputMode(MapOutputMode mode) throws StatusException
	{
		int status = NativeMethods.xnNodeQueryAddSupportedMapOutputMode(toNative(), mode.getXRes(), mode.getYRes(), mode.getFPS());
		WrapperUtils.throwOnError(status);
	}

	public void addSupportedMinUserPositions(int count) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetSupportedMinUserPositions(toNative(), count);
		WrapperUtils.throwOnError(status);
	}

	public void setExistingNodeOnly(boolean existingOnly) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetExistingNodeOnly(toNative(), existingOnly);
		WrapperUtils.throwOnError(status);
	}

	public void setNonExistingNodeOnly(boolean nonExistingOnly) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetNonExistingNodeOnly(toNative(), nonExistingOnly);
		WrapperUtils.throwOnError(status);
	}
	
	public void addNeededNode(ProductionNode node) throws StatusException
	{
		int status = NativeMethods.xnNodeQueryAddNeededNode(toNative(), node.getName());
		WrapperUtils.throwOnError(status);
	}
	
	public void setCreationInfo(String creationInfo) throws StatusException
	{
		int status = NativeMethods.xnNodeQuerySetCreationInfo(toNative(), creationInfo);
		WrapperUtils.throwOnError(status);
	}

	@Override
	protected void freeObject(long ptr) 
	{
		NativeMethods.xnNodeQueryFree(ptr);
	}
	
	private static long allocate() throws StatusException
	{
		OutArg<Long> pQuery = new OutArg<Long>();
		int status = NativeMethods.xnNodeQueryAllocate(pQuery);
		WrapperUtils.throwOnError(status);
		return pQuery.value;
	}

}
