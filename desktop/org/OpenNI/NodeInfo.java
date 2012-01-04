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

public class NodeInfo extends ObjectWrapper 
{
	public ProductionNodeDescription getDescription()
	{
		return NativeMethods.xnNodeInfoGetDescription(toNative());
	}
	
	public String getInstanceName()
	{
		return NativeMethods.xnNodeInfoGetInstanceName(toNative());
	}
	
	public String getCreationInfo()
	{
		return NativeMethods.xnNodeInfoGetCreationInfo(toNative());
	}
	
	public NodeInfoList getNeededNodes() throws GeneralException
	{
		return new NodeInfoList(NativeMethods.xnNodeInfoGetNeededNodes(toNative()));
	}
	
	public ProductionNode getInstance() throws GeneralException
	{
		long hNode = NativeMethods.xnNodeInfoGetRefHandle(toNative());
		return Context.createProductionNodeFromNative(hNode);
	}
	
	@Override
	public String toString() 
	{
		OutArg<String> result = new OutArg<String>();
		NativeMethods.xnNodeInfoGetTreeStringRepresentation(toNative(), result);
		return result.value;
	}
	
	protected NodeInfo(long pNodeInfo) throws GeneralException 
	{
		super(pNodeInfo);
	}
	
	@Override
	protected void freeObject(long ptr) 
	{
		// no need to free
	}
}
