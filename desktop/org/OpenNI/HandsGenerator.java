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

public class HandsGenerator extends Generator
{

	HandsGenerator(Context context, long nodeHandle, boolean addRef)
			throws GeneralException
	{
		super(context, nodeHandle, addRef);
		// TODO Auto-generated constructor stub
		
		handCreateEvent = new Observable<ActiveHandEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterHandCallbacks(toNative(), this, "callback", null, null, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterHandCallbacks(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int id, Point3D point, float time)
			{
				notify(new ActiveHandEventArgs(id, point, time));
			}
		};
		handUpdateEvent = new Observable<ActiveHandEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterHandCallbacks(toNative(), this, null, "callback", null, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterHandCallbacks(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int id, Point3D point, float time)
			{
				notify(new ActiveHandEventArgs(id, point, time));
			}
		};
		handDestroyEvent = new Observable<InactiveHandEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterHandCallbacks(toNative(), this, null, null, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterHandCallbacks(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int id, float time)
			{
				notify(new InactiveHandEventArgs(id, time));
			}
		};
	}

	public static HandsGenerator create(Context context, Query query, EnumerationErrors errors) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateHandsGenerator(context.toNative(), handle,
			query == null ? 0 : query.toNative(),
			errors == null ? 0 : errors.toNative());
		WrapperUtils.throwOnError(status);
		HandsGenerator result = (HandsGenerator)context.createProductionNodeObject(handle.value, NodeType.HANDS);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public static HandsGenerator create(Context context, Query query) throws GeneralException
	{
		return create(context, query, null);
	}

	public static HandsGenerator create(Context context) throws GeneralException
	{
		return create(context, null, null);
	}

	public void StopTracking(int id) throws StatusException
	{
		int status = NativeMethods.xnStopTracking(toNative(), id);
		WrapperUtils.throwOnError(status);
	}
	public void StopTrackingAll() throws StatusException
	{
		int status = NativeMethods.xnStopTrackingAll(toNative());
		WrapperUtils.throwOnError(status);
	}
	public void StartTracking(Point3D position) throws StatusException
	{
		int status = NativeMethods.xnStartTracking(toNative(), position.getX(), position.getY(), position.getZ());
		WrapperUtils.throwOnError(status);
	}
	public void SetSmoothing(float factor) throws StatusException
	{
		int status = NativeMethods.xnSetTrackingSmoothing(toNative(), factor);
		WrapperUtils.throwOnError(status);
	}
	
	public HandTouchingFOVEdgeCapability getHandTouchingFOVEdgeCapability() throws StatusException
	{
		return new HandTouchingFOVEdgeCapability(this);
	}

	// Events
	public IObservable<ActiveHandEventArgs> getHandCreateEvent()
	{
		return handCreateEvent;
	}
	public IObservable<ActiveHandEventArgs> getHandUpdateEvent()
	{
		return handUpdateEvent;
	}
	public IObservable<InactiveHandEventArgs> getHandDestroyEvent()
	{
		return handDestroyEvent;
	}
	
	private Observable<ActiveHandEventArgs> handCreateEvent;
	private Observable<ActiveHandEventArgs> handUpdateEvent;
	private Observable<InactiveHandEventArgs> handDestroyEvent;
}
