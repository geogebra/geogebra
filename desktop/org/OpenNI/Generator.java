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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Generator extends ProductionNode 
{
	Generator(Context context, long nodeHandle, boolean addRef) throws GeneralException 
	{
		super(context, nodeHandle, addRef);
		
		this.generationRunningChanged = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToGenerationRunningChange(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromGenerationRunningChange(toNative(), hCallback);
			}
		};
		
		this.newDataAvailable = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToNewDataAvailable(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromNewDataAvailable(toNative(), hCallback);
			}
		};		
	}

	public void startGenerating() throws StatusException
	{
		int status = NativeMethods.xnStartGenerating(this.toNative());
		WrapperUtils.throwOnError(status);
	}

	public boolean isGenerating()
	{
		return NativeMethods.xnIsGenerating(this.toNative());
	}

	public void stopGenerating() throws StatusException
	{
		int status = NativeMethods.xnStopGenerating(this.toNative());
		WrapperUtils.throwOnError(status);
	}

	public IStateChangedObservable getGenerationRunningChangedEvent()
	{
		return this.generationRunningChanged;
	}

	public boolean isNewDataAvailable()
	{
		OutArg<Long> timestamp = new OutArg<Long>();
		return NativeMethods.xnIsNewDataAvailable(this.toNative(), timestamp);
	}

	public long getAvailableTimestamp()
	{
		OutArg<Long> timestamp = new OutArg<Long>();
		NativeMethods.xnIsNewDataAvailable(this.toNative(), timestamp);
		return timestamp.value;
	}

	public IStateChangedObservable getNewDataAvailableEvent()
	{
		return this.newDataAvailable;
	}

	public void waitAndUpdateData() throws StatusException
	{
		int status = NativeMethods.xnWaitAndUpdateData(this.toNative());
		WrapperUtils.throwOnError(status);
	}

	public boolean isDataNew()
	{
		return NativeMethods.xnIsDataNew(this.toNative());
	}

	public int getDataSize()
	{
		return NativeMethods.xnGetDataSize(this.toNative());
	}
	
	public long getDataPtr()
	{
		return NativeMethods.xnGetData(this.toNative());
	}

	public ByteBuffer createDataByteBuffer()
	{
		int size = getDataSize();
		ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		NativeMethods.copyToBuffer(buffer, getDataPtr(), size);
		return buffer;
	}
	
	public void copyDataToBuffer(ByteBuffer buffer, int size)
	{
		NativeMethods.copyToBuffer(buffer, getDataPtr(), size);
	} 	

	public long getTimestamp()
	{
		return NativeMethods.xnGetTimestamp(this.toNative());
	}

	public int getFrameID()
	{
		return NativeMethods.xnGetFrameID(this.toNative());
	}

	public MirrorCapability getMirrorCapability() throws StatusException
	{
		return new MirrorCapability(this);
	}

	public AlternativeViewpointCapability getAlternativeViewpointCapability() throws StatusException
	{
		return new AlternativeViewpointCapability(this);
	}

	public FrameSyncCapability getFrameSyncCapability() throws StatusException
	{
		return new FrameSyncCapability(this);
	}

	private StateChangedObservable generationRunningChanged;
	private StateChangedObservable newDataAvailable;
}
