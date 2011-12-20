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

public class HandTouchingFOVEdgeCapability extends CapabilityBase
{
	public HandTouchingFOVEdgeCapability(ProductionNode node) throws StatusException
	{
		super(node);
		
		// Events
		handTouchingFOVEdgeEvent = new Observable<ActiveHandDirectionEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterToHandTouchingFOVEdge(toNative(), this, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromHandTouchingFOVEdge(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int id, Point3D position, float time, int direction)
			{
				notify(new ActiveHandDirectionEventArgs(id, position, time, Direction.fromNative(direction)));
			}
		};
	}

	// Events
	public IObservable<ActiveHandDirectionEventArgs> getHandTouchingFOVEdgeEvent()
	{
		return handTouchingFOVEdgeEvent;
	}
	
	private Observable<ActiveHandDirectionEventArgs> handTouchingFOVEdgeEvent;
}
