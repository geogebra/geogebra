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

public class ProductionNode extends NodeWrapper
{
	ProductionNode(Context context, long nodeHandle, boolean addRef) throws StatusException
	{
		super(context, nodeHandle, addRef);
	}
	
	public static ProductionNode fromNative(long hNode) throws GeneralException
	{
		return Context.createProductionNodeFromNative(hNode);
	}
	
	public NodeInfo getInfo() throws GeneralException
	{
		return new NodeInfo(NativeMethods.xnGetNodeInfo(toNative()));
	}
	
	public void addNeededNode(ProductionNode needed) throws StatusException
	{
		int status = NativeMethods.xnAddNeededNode(toNative(), needed.toNative());
		WrapperUtils.throwOnError(status);
	}

	public void removeNeededNode(ProductionNode needed) throws StatusException
	{
		int status = NativeMethods.xnRemoveNeededNode(toNative(), needed.toNative());
		WrapperUtils.throwOnError(status);
	}

	public boolean isCapabilitySupported(String capabilityName)
	{
		return NativeMethods.xnIsCapabilitySupported(this.toNative(), capabilityName);
	}

	public void setIntProperty(String propName, long value) throws StatusException
	{
		int status = NativeMethods.xnSetIntProperty(this.toNative(), propName, value);
		WrapperUtils.throwOnError(status);
	}

	public void setRealProperty(String propName, double value) throws StatusException
	{
		int status = NativeMethods.xnSetRealProperty(this.toNative(), propName, value);
		WrapperUtils.throwOnError(status);
	}

	public void setStringProperty(String propName, String value) throws StatusException
	{
		int status = NativeMethods.xnSetStringProperty(this.toNative(), propName, value);
		WrapperUtils.throwOnError(status);
	}

	public void setGeneralProperty(String propName, int size, long buff) throws StatusException
	{
		int status = NativeMethods.xnSetGeneralProperty(this.toNative(), propName, size, buff);
		WrapperUtils.throwOnError(status);
	}

	public void setGeneralProperty(String propName, byte[] buffer) throws StatusException
	{
		int status = NativeMethods.xnSetGeneralPropertyArray(this.toNative(), propName, buffer);
		WrapperUtils.throwOnError(status);
	}

	public long getIntProperty(String propName) throws StatusException
	{
		OutArg<Long> value = new OutArg<Long>();
		int status = NativeMethods.xnGetIntProperty(this.toNative(), propName, value);
		WrapperUtils.throwOnError(status);
		return value.value.longValue();
	}

	public double getRealProperty(String propName) throws StatusException
	{
		OutArg<Double> value = new OutArg<Double>();
		int status = NativeMethods.xnGetRealProperty(this.toNative(), propName, value);
		WrapperUtils.throwOnError(status);
		return value.value.doubleValue();
	}

	public String getStringProperty(String propName) throws StatusException
	{
		OutArg<String> value = new OutArg<String>();
		int status = NativeMethods.xnGetStringProperty(this.toNative(), propName, value);
		WrapperUtils.throwOnError(status);
		return value.value;
	}

	public void getGeneralProperty(String propName, int size, long buff) throws StatusException
	{
		int status = NativeMethods.xnGetGeneralProperty(this.toNative(), propName, size, buff);
		WrapperUtils.throwOnError(status);
	}

	public void getGeneralProperty(String propName, byte[] buffer) throws StatusException
	{
		int status = NativeMethods.xnGetGeneralPropertyArray(this.toNative(), propName, buffer);
		WrapperUtils.throwOnError(status);
	}

	public LockHandle lockForChanges() throws StatusException
	{
		OutArg<Integer> handle = new OutArg<Integer>();
		int status = NativeMethods.xnLockNodeForChanges(this.toNative(), handle);
		WrapperUtils.throwOnError(status);
		return new LockHandle(handle.value.intValue());
	}

	public void unlockForChanges(LockHandle lockHandle) throws StatusException
	{
		int status = NativeMethods.xnUnlockNodeForChanges(this.toNative(), lockHandle.toNative());
		WrapperUtils.throwOnError(status);
	}

	public void lockedNodeStartChanges(LockHandle lockHandle) throws StatusException
	{
		int status = NativeMethods.xnLockedNodeStartChanges(this.toNative(), lockHandle.toNative());
		WrapperUtils.throwOnError(status);
	}

	public void lockedNodeEndChanges(LockHandle lockHandle) throws StatusException
	{
		int status = NativeMethods.xnLockedNodeEndChanges(this.toNative(), lockHandle.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public ErrorStateCapability getErrorStateCapability() throws StatusException
	{
		return new ErrorStateCapability(this);
	}

	public GeneralIntCapability getGeneralIntCapability(Capability capability) throws StatusException
	{
		return new GeneralIntCapability(this, capability);
	}
}
