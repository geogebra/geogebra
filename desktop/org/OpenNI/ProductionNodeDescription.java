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

public class ProductionNodeDescription 
{
	public ProductionNodeDescription(NodeType type, String vendor, String name, Version version)
	{
		this.type = type;
		this.vendor = vendor;
		this.name = name;
		this.version = version;
	}

	@SuppressWarnings("unused")
	private ProductionNodeDescription(int type, String vendor, String name, Version version)
	{
		this(new NodeType(type), vendor, name, version);
	}

	public NodeType getType() { return this.type; }
	public String getVendor() { return this.vendor; }
	public String getName() { return this.name; }
	public Version getVersion() { return this.version; }
	
	protected long createNative()
	{
		return NativeMethods.createProductionNodeDescription(type.toNative(), vendor, name, version.getMajor(), version.getMinor(), version.getMaintenance(), version.getBuild());
	}
	
	protected static void freeNative(long pNative)
	{
		NativeMethods.freeProductionNodeDescription(pNative);
	}

	private NodeType type;
	private String vendor;
	private String name;
	private Version version; 
}
