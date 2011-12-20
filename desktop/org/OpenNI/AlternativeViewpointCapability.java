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

public class AlternativeViewpointCapability extends CapabilityBase
{
	public AlternativeViewpointCapability(ProductionNode node) throws StatusException
	{
		super(node);
		
		this.viewPointChanged = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToViewPointChange(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromViewPointChange(toNative(), hCallback);
			}
		};
	}
	
	boolean isViewpointSupported(ProductionNode other)
	{
		return NativeMethods.xnIsViewPointSupported(toNative(), other.toNative());
	}
	
	void setViewpoint(ProductionNode other) throws StatusException
	{
		int status = NativeMethods.xnSetViewPoint(toNative(), other.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	void resetViewpoint() throws StatusException
	{
		int status = NativeMethods.xnResetViewPoint(toNative());
		WrapperUtils.throwOnError(status);
	}
	
	boolean isViewpointAs(ProductionNode other)
	{
		return NativeMethods.xnIsViewPointAs(toNative(), other.toNative());
	}

	public IStateChangedObservable getViewPointChangedEvent() { return this.viewPointChanged; }

	private StateChangedObservable viewPointChanged;
}
