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

public class ScriptNode extends ProductionNode
{
	ScriptNode(Context context, long nodeHandle, boolean addRef) throws StatusException
	{
		super(context, nodeHandle, addRef);
	}

	public static ScriptNode create(Context context, String formatName) throws GeneralException
	{
		OutArg<Long> phScriptNode = new OutArg<Long>();
		int status = NativeMethods.xnCreateScriptNode(context.toNative(), formatName, phScriptNode);
		WrapperUtils.throwOnError(status);
		ScriptNode ScriptNode = (ScriptNode)context.createProductionNodeObject(phScriptNode.value, NodeType.SCRIPT_NODE);
		NativeMethods.xnProductionNodeRelease(phScriptNode.value);
		return ScriptNode;
	}

	public String getSupportedFormat()
	{
		return NativeMethods.xnScriptNodeGetSupportedFormat(toNative());
	}
	
	public void loadScriptFromFile(String fileName) throws StatusException
	{
		int status = NativeMethods.xnLoadScriptFromFile(toNative(), fileName);
		WrapperUtils.throwOnError(status);
	}

	public void loadScriptFromString(String script) throws StatusException
	{
		int status = NativeMethods.xnLoadScriptFromString(toNative(), script);
		WrapperUtils.throwOnError(status);
	}
	
	public void Run(EnumerationErrors errors) throws StatusException
	{
		int status = NativeMethods.xnScriptNodeRun(toNative(), errors.toNative());
		WrapperUtils.throwOnError(status);
	}
}
