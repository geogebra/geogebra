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

public class Player extends ProductionNode 
{
	Player(Context context, long nodeHandle, boolean addRef) throws StatusException 
	{
		super(context, nodeHandle, addRef);
		
		this.eofReached = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToEndOfFileReached(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromEndOfFileReached(toNative(), hCallback);
			}
		};
	}

	public static Player create(Context context, String formatName) throws GeneralException
	{
		OutArg<Long> phPlayer = new OutArg<Long>();
		int status = NativeMethods.xnCreatePlayer(context.toNative(), formatName, phPlayer);
		WrapperUtils.throwOnError(status);
		Player player = (Player)context.createProductionNodeObject(phPlayer.value, NodeType.PLAYER);
		NativeMethods.xnProductionNodeRelease(phPlayer.value);
		return player;
	}

	public String getFormat()
	{
		return NativeMethods.xnGetPlayerSupportedFormat(toNative());
	}
	
	public void setSource(RecordMedium medium, String name) throws StatusException
	{
		int status = NativeMethods.xnSetPlayerSource(toNative(), medium.toNative(), name);
		WrapperUtils.throwOnError(status);
	}
	
	public RecordMedium getSourceMedium() throws StatusException
	{
		OutArg<Integer> pMedium = new OutArg<Integer>();
		OutArg<String> pDest = new OutArg<String>();
		int status = NativeMethods.xnGetPlayerSource(toNative(), pMedium, pDest);
		WrapperUtils.throwOnError(status);
		return RecordMedium.fromNative(pMedium.value);
	}

	public String getSource() throws StatusException
	{
		OutArg<Integer> pMedium = new OutArg<Integer>();
		OutArg<String> pDest = new OutArg<String>();
		int status = NativeMethods.xnGetPlayerSource(toNative(), pMedium, pDest);
		WrapperUtils.throwOnError(status);
		return pDest.value;
	}

	public void setRepeat(boolean repeat) throws StatusException
	{
		int status = NativeMethods.xnSetPlayerRepeat(toNative(), repeat);
		WrapperUtils.throwOnError(status);
	}
	
	public void readNext() throws StatusException
	{
		int status = NativeMethods.xnPlayerReadNext(toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public void seekToTimestamp(PlayerSeekOrigin origin, long offset) throws StatusException
	{
		int status = NativeMethods.xnSeekPlayerToTimeStamp(toNative(), offset, origin.toNative());
		WrapperUtils.throwOnError(status);
	}

	public void seekToFrame(ProductionNode node, PlayerSeekOrigin origin, int offset) throws StatusException
	{
		int status = NativeMethods.xnSeekPlayerToFrame(toNative(), node.getName(), offset, origin.toNative());
		WrapperUtils.throwOnError(status);
	}

	public long tellTimestamp() throws StatusException
	{
		OutArg<Long> pnTimestamp = new OutArg<Long>();
		int status = NativeMethods.xnTellPlayerTimestamp(toNative(), pnTimestamp);
		WrapperUtils.throwOnError(status);
		return pnTimestamp.value;
	}

	public int tellFrame(ProductionNode node) throws StatusException
	{
		OutArg<Integer> pnFrameID = new OutArg<Integer>();
		int status = NativeMethods.xnTellPlayerFrame(toNative(), node.getName(), pnFrameID);
		WrapperUtils.throwOnError(status);
		return pnFrameID.value;
	}
	
	public int getNumberOfFrames(ProductionNode node) throws StatusException
	{
		OutArg<Integer> pnFrames = new OutArg<Integer>();
		int status = NativeMethods.xnGetPlayerNumFrames(toNative(), node.getName(), pnFrames);
		WrapperUtils.throwOnError(status);
		return pnFrames.value;
	}

	public NodeInfoList enumerateNodes() throws StatusException
	{
		OutArg<Long> ppList = new OutArg<Long>();
		int status = NativeMethods.xnEnumeratePlayerNodes(toNative(), ppList);
		WrapperUtils.throwOnError(status);
		return new NodeInfoList(ppList.value);
	}
	
	public boolean isEOF()
	{
		return NativeMethods.xnIsPlayerAtEOF(toNative());
	}
	
	public IStateChangedObservable getEOFReachedEvent() { return this.eofReached; }
	
	public double getPlaybackSpeed()
	{
		return NativeMethods.xnGetPlaybackSpeed(toNative());
	}
	
	public void setPlaybackSpeed(double speed) throws StatusException
	{
		int status = NativeMethods.xnSetPlaybackSpeed(toNative(), speed);
		WrapperUtils.throwOnError(status);
	}
	
	private StateChangedObservable eofReached;
}
