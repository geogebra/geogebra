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

public class IRGenerator extends MapGenerator
{
	IRGenerator(Context context, long nodeHandle, boolean addRef) throws GeneralException
	{
		super(context, nodeHandle, addRef);
		// TODO Auto-generated constructor stub
	}

	public static IRGenerator create(Context context, Query query, EnumerationErrors errors) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateIRGenerator(context.toNative(), handle,
			query == null ? 0 : query.toNative(),
			errors == null ? 0 : errors.toNative());
		WrapperUtils.throwOnError(status);
		IRGenerator result = (IRGenerator)context.createProductionNodeObject(handle.value, NodeType.IR);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public static IRGenerator create(Context context, Query query) throws GeneralException
	{
		return create(context, query, null);
	}

	public static IRGenerator create(Context context) throws GeneralException
	{
		return create(context, null, null);
	}
	
	public IRMap getIRMap() throws GeneralException
	{
		int frameID = getFrameID();
		
		if ((this.currIRMap == null) || (this.currIRMapFrameID != frameID))
		{
			long ptr = NativeMethods.xnGetIRMap(toNative());
			MapOutputMode mode = getMapOutputMode();
			this.currIRMap = new IRMap(ptr, mode.getXRes(), mode.getYRes());
			this.currIRMapFrameID = frameID; 
		}

		return this.currIRMap;
	}
	
	public void getMetaData(IRMetaData IRMD)
	{
		NativeMethods.xnGetIRMetaData(this.toNative(), IRMD);
	}

	public IRMetaData getMetaData()
	{
		IRMetaData IRMD = new IRMetaData();
		getMetaData(IRMD);
		return IRMD;
	}

	private IRMap currIRMap;
	private int currIRMapFrameID;
}
