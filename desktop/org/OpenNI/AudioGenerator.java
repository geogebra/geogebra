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

public class AudioGenerator extends Generator
{
	public AudioGenerator(Context context, long nodeHandle, boolean addRef) throws GeneralException
	{
		super(context, nodeHandle, addRef);
		
		this.waveOutputModeChanged = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToWaveOutputModeChanges(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromWaveOutputModeChanges(toNative(), hCallback);
			}
		}; 
	}

	public static AudioGenerator create(Context context, Query query, EnumerationErrors errors) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateAudioGenerator(context.toNative(), handle,
			query == null ? 0 : query.toNative(),
			errors == null ? 0 : errors.toNative());
		WrapperUtils.throwOnError(status);
		AudioGenerator result = (AudioGenerator)context.createProductionNodeObject(handle.value, NodeType.AUDIO);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public static AudioGenerator create(Context context, Query query) throws GeneralException
	{
		return create(context, query, null);
	}

	public static AudioGenerator create(Context context) throws GeneralException
	{
		return create(context, null, null);
	}
	
	public WaveOutputMode[] getSupportedMapOutputModes() throws StatusException
	{
		int count = NativeMethods.xnGetSupportedWaveOutputModesCount(this.toNative());
		WaveOutputMode[] supportedModes = new WaveOutputMode[count];
		int status = NativeMethods.xnGetSupportedWaveOutputModes(this.toNative(), supportedModes);
		WrapperUtils.throwOnError(status);
		return supportedModes;
	}

	public WaveOutputMode getWaveOutputMode() throws StatusException
	{
		OutArg<Integer> sampleRate = new OutArg<Integer>();
		OutArg<Short> bitsPerSample = new OutArg<Short>();
		OutArg<Byte> numberOfChannels = new OutArg<Byte>();
		int status = NativeMethods.xnGetWaveOutputMode(this.toNative(), sampleRate, bitsPerSample, numberOfChannels);
		WrapperUtils.throwOnError(status);
		return new WaveOutputMode(sampleRate.value, bitsPerSample.value, numberOfChannels.value);
	}
	
	public void setWaveOutputMode(WaveOutputMode mode) throws StatusException
	{
		int status = NativeMethods.xnSetWaveOutputMode(this.toNative(), mode.getSampleRate(), mode.getBitsPerSample(), mode.getNumberOfChannels());
		WrapperUtils.throwOnError(status);
	}

	public IStateChangedObservable getMapOutputModeChangedEvent() { return this.waveOutputModeChanged; }

	public void getMetaData(AudioMetaData audioMD)
	{
		NativeMethods.xnGetAudioMetaData(this.toNative(), audioMD);
	}

	public AudioMetaData getMetaData()
	{
		AudioMetaData audioMD = new AudioMetaData();
		getMetaData(audioMD);
		return audioMD;
	}

	private StateChangedObservable waveOutputModeChanged;
}
