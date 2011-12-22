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

public class GestureGenerator extends Generator
{
	GestureGenerator(Context context, long nodeHandle, boolean addRef) throws GeneralException 
	{
		super(context, nodeHandle, addRef);
		
		gestureRecognizedEvent = new Observable<GestureRecognizedEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterGestureCallbacks(toNative(), this, "callback", null, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterHandCallbacks(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(String gesture, Point3D idPosition, Point3D endPosition)
			{
				notify(new GestureRecognizedEventArgs(gesture, idPosition, endPosition));
			}
		};
		gestureProgressEvent = new Observable<GestureProgressEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterGestureCallbacks(toNative(), this, null, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterHandCallbacks(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(String gesture, Point3D position, float progress)
			{
				notify(new GestureProgressEventArgs(gesture, position, progress));
			}
		};
		gestureIntermediateStageCompletedEvent = new Observable<GesturePositionEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterToGestureIntermediateStageCompleted(toNative(), this, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromGestureIntermediateStageCompleted(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(String gesture, Point3D position)
			{
				notify(new GesturePositionEventArgs(gesture, position));
			}
		};
		gestureReadyForNextIntermediateStageEvent = new Observable<GesturePositionEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterToGestureReadyForNextIntermediateStage(toNative(), this, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromGestureReadyForNextIntermediateStage(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(String gesture, Point3D position)
			{
				notify(new GesturePositionEventArgs(gesture, position));
			}
		};
		gestureChangedEvent = new StateChangedObservable()
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback)
			{
				return NativeMethods.xnRegisterToGestureChange(toNative(), this, cb, phCallback);
			}
			@Override
			protected void unregisterNative(long hCallback)
			{
				NativeMethods.xnUnregisterFromGestureChange(toNative(), hCallback);
			}
			
		};
	}
	
	public static GestureGenerator create(Context context, Query query, EnumerationErrors errors) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateGestureGenerator(context.toNative(), handle,
			query == null ? 0 : query.toNative(),
			errors == null ? 0 : errors.toNative());
		WrapperUtils.throwOnError(status);
		GestureGenerator result = (GestureGenerator)context.createProductionNodeObject(handle.value, NodeType.GESTURE);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public static GestureGenerator create(Context context, Query query) throws GeneralException
	{
		return create(context, query, null);
	}

	public static GestureGenerator create(Context context) throws GeneralException
	{
		return create(context, null, null);
	}
	
	public void addGesture(String gesture) throws StatusException
	{
		int status = NativeMethods.xnAddGesture(toNative(), gesture);
		WrapperUtils.throwOnError(status);
	}
	public void addGesture(String gesture, BoundingBox3D area) throws StatusException
	{
		if (area == null)
		{
			addGesture(gesture);
			return;
		}
		int status = NativeMethods.xnAddGesture(toNative(), gesture, area.getMins().getX(), area.getMins().getY(), area.getMins().getZ(), area.getMaxs().getX(), area.getMaxs().getY(), area.getMaxs().getZ());
		WrapperUtils.throwOnError(status);
	}
	public void removeGesture(String gesture) throws StatusException
	{
		int status = NativeMethods.xnRemoveGesture(toNative(), gesture);
		WrapperUtils.throwOnError(status);
	}
	
	public int getNumberOfAvailableGestures()
	{
		return NativeMethods.xnGetNumberOfAvailableGestures(toNative());
	}
	
	public boolean isGestureAvailable(String gesture)
	{
		return NativeMethods.xnIsGestureAvailable(toNative(), gesture);
	}
	public boolean isGestureProgressSupported(String gesture)
	{
		return NativeMethods.xnIsGestureProgressSupported(toNative(), gesture);
	}

	public String[] enumerateAllGestures() throws StatusException
	{
		OutArg<String[]> gestures = new OutArg<String[]>();
		int status = NativeMethods.xnEnumerateAllGestures(toNative(), gestures);
		WrapperUtils.throwOnError(status);
		return gestures.value;
	}
	
	public String[] getAllActiveGestures() throws StatusException
	{
		OutArg<String[]> gestures = new OutArg<String[]>();
		int status = NativeMethods.xnGetAllActiveGestures(toNative(), gestures);
		WrapperUtils.throwOnError(status);
		return gestures.value;
	}
	
	// Events
	public IObservable<GestureRecognizedEventArgs> getGestureRecognizedEvent()
	{
		return gestureRecognizedEvent;
	}
	public IObservable<GestureProgressEventArgs> getGestureProgressEvent()
	{
		return gestureProgressEvent;
	}
	public IObservable<GesturePositionEventArgs> getGestureIntermediateStageCompletedEvent()
	{
		return gestureIntermediateStageCompletedEvent;
	}
	public IObservable<GesturePositionEventArgs> getGestureReadyForNextIntermediateStageEvent()
	{
		return gestureReadyForNextIntermediateStageEvent;
	}
	public IStateChangedObservable getGestureChangedEvent()
	{
		return gestureChangedEvent;
	}
	
	private Observable<GestureRecognizedEventArgs> gestureRecognizedEvent;
	private Observable<GestureProgressEventArgs> gestureProgressEvent;
	private Observable<GesturePositionEventArgs> gestureIntermediateStageCompletedEvent;
	private Observable<GesturePositionEventArgs> gestureReadyForNextIntermediateStageEvent;
	private StateChangedObservable gestureChangedEvent;
}
