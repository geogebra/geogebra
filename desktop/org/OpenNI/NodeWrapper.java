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

public class NodeWrapper extends ObjectWrapper
{
	NodeWrapper(Context context, long hNode, boolean addRef) throws StatusException
	{
		super(hNode);
		
		this.context = context;
		
		if (addRef)
		{
			WrapperUtils.throwOnError(NativeMethods.xnProductionNodeAddRef(hNode));
		}
	}
	
	public Context getContext()
	{
		return this.context;
	}
	
	public String getName()
	{
		return NativeMethods.xnGetNodeName(toNative());
	}

	protected void freeObject(long ptr)
	{
		NativeMethods.xnProductionNodeRelease(ptr);
	}
	
	private Context context;
}
