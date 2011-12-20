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

public class CroppingCapability extends CapabilityBase
{
	public CroppingCapability(ProductionNode node) throws StatusException
	{
		super(node);
		
		this.croppingChanged = new StateChangedObservable()
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToCroppingChange(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromCroppingChange(toNative(), hCallback);
			}
		};
	}
	
	public void setCropping(Cropping cropping) throws StatusException
	{
		int status = NativeMethods.xnSetCropping(toNative(), cropping.getXOffset(), cropping.getYOffset(), cropping.getXSize(), cropping.getYSize(), cropping.isEnabled());
		WrapperUtils.throwOnError(status);
	}
	
	public Cropping getCropping() throws StatusException
	{
		OutArg<Integer> xOffset = new OutArg<Integer>();
		OutArg<Integer> yOffset = new OutArg<Integer>();
		OutArg<Integer> xSize = new OutArg<Integer>();
		OutArg<Integer> ySize = new OutArg<Integer>();
		OutArg<Boolean> isEnabled = new OutArg<Boolean>();
		int status = NativeMethods.xnGetCropping(toNative(), xOffset, yOffset, xSize, ySize, isEnabled);
		WrapperUtils.throwOnError(status);
		return new Cropping(xOffset.value, yOffset.value, xSize.value, ySize.value, isEnabled.value);
	}
	
	public IStateChangedObservable getCroppingChangedEvent() { return this.croppingChanged; }

	private StateChangedObservable croppingChanged;
}
