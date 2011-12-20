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

public class MirrorCapability extends CapabilityBase
{
	public MirrorCapability(ProductionNode node) throws StatusException
	{
		super(node);
		
		this.mirrorChanged = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToMirrorChange(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromMirrorChange(toNative(), hCallback);
			}
		};
	}
	
	public boolean isMirrored()
	{
		return NativeMethods.xnIsMirrored(toNative());
	}
	
	public void setMirror(boolean isMirrored) throws StatusException
	{
		int status = NativeMethods.xnSetMirror(toNative(), isMirrored);
		WrapperUtils.throwOnError(status);
	}

	public IStateChangedObservable getMirrorChangedEvent() { return this.mirrorChanged; }

	private StateChangedObservable mirrorChanged;
}
