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

public class Recorder extends ProductionNode
{
	Recorder(Context context, long nodeHandle, boolean addRef) throws StatusException
	{
		super(context, nodeHandle, addRef);
	}

	public static Recorder create(Context context, String formatName) throws GeneralException
	{
		OutArg<Long> phRecorder = new OutArg<Long>();
		int status = NativeMethods.xnCreateRecorder(context.toNative(), formatName, phRecorder);
		WrapperUtils.throwOnError(status);
		Recorder recorder = (Recorder)context.createProductionNodeObject(phRecorder.value, NodeType.RECORDER);
		NativeMethods.xnProductionNodeRelease(phRecorder.value);
		return recorder;
	}
	
	public String getFormat()
	{
		return NativeMethods.xnGetRecorderFormat(toNative());
	}
	
	public void setDestination(RecordMedium medium, String name) throws StatusException
	{
		int status = NativeMethods.xnSetRecorderDestination(toNative(), medium.toNative(), name);
		WrapperUtils.throwOnError(status);
	}
	
	public RecordMedium getDestinationMedium() throws StatusException
	{
		OutArg<Integer> pMedium = new OutArg<Integer>();
		OutArg<String> pDest = new OutArg<String>();
		int status = NativeMethods.xnGetRecorderDestination(toNative(), pMedium, pDest);
		WrapperUtils.throwOnError(status);
		return RecordMedium.fromNative(pMedium.value);
	}

	public String getDestination() throws StatusException
	{
		OutArg<Integer> pMedium = new OutArg<Integer>();
		OutArg<String> pDest = new OutArg<String>();
		int status = NativeMethods.xnGetRecorderDestination(toNative(), pMedium, pDest);
		WrapperUtils.throwOnError(status);
		return pDest.value;
	}
	
	public void addNodeToRecording(ProductionNode node, CodecID codec) throws StatusException
	{
		int status = NativeMethods.xnAddNodeToRecording(toNative(), node.toNative(), codec.toNative());
		WrapperUtils.throwOnError(status);
	}

	public void addNodeToRecording(ProductionNode node) throws StatusException
	{
		addNodeToRecording(node, CodecID.Null);
	}

	public void removeNodeToRecording(ProductionNode node) throws StatusException
	{
		int status = NativeMethods.xnRemoveNodeFromRecording(toNative(), node.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public void Record() throws StatusException
	{
		int status = NativeMethods.xnRecord(toNative());
		WrapperUtils.throwOnError(status);
	}
}
