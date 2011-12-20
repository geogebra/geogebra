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

public class UserPositionCapability extends CapabilityBase
{
	public UserPositionCapability(ProductionNode node) throws StatusException
	{
		super(node);
		
		this.userPositionChanged = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToUserPositionChange(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromUserPositionChange(toNative(), hCallback);
			}
		};
	}

	public int getSupportedCount()
	{
		return NativeMethods.xnGetSupportedUserPositionsCount(toNative());
	}
	
	public void setUserPosition(int index, BoundingBox3D position) throws StatusException
	{
		Point3D leftBottomNear = position.getLeftBottomNear();
		Point3D rightTopFar = position.getRightTopFar();
		int status = NativeMethods.xnSetUserPosition(toNative(), index, leftBottomNear.getX(), leftBottomNear.getY(), leftBottomNear.getZ(), rightTopFar.getX(), rightTopFar.getY(), rightTopFar.getZ());
		WrapperUtils.throwOnError(status);
	}
	
	public BoundingBox3D getUserPosition(int index) throws StatusException
	{
		OutArg<BoundingBox3D> position = new OutArg<BoundingBox3D>();
		int status = NativeMethods.xnGetUserPosition(toNative(), index, position);
		WrapperUtils.throwOnError(status);
		return position.value;
	}
	
	public IStateChangedObservable getUserPositionChangedEvent() { return this.userPositionChanged; }
	
	private StateChangedObservable userPositionChanged;
}
