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

public class NodeType 
{
	public static NodeType INVALID = new NodeType(0);

	/** A device node **/
	public static NodeType DEVICE = new NodeType(1);

	/** A depth generator **/
	public static NodeType DEPTH = new NodeType(2);

	/** An image generator **/
	public static NodeType IMAGE = new NodeType(3);

	/** An audio generator **/
	public static NodeType AUDIO = new NodeType(4);

	/** An IR generator **/
	public static NodeType IR = new NodeType(5);

	/** A user generator **/
	public static NodeType USER = new NodeType(6);

	/** A recorder **/
	public static NodeType RECORDER = new NodeType(7);

	/** A player **/
	public static NodeType PLAYER = new NodeType(8);

	/** A gesture generator **/
	public static NodeType GESTURE = new NodeType(9);

	/** A scene analyzer **/
	public static NodeType SCENE = new NodeType(10);

	/** A hands generator **/
	public static NodeType HANDS = new NodeType(11);

	/** A Codec **/
	public static NodeType CODEC = new NodeType(12);

	/** A general production node **/
	public static NodeType PRODUCTION_NODE = new NodeType(13);

	/** A general generator **/
	public static NodeType GENERATOR = new NodeType(14);

	/** A general map generator **/
	public static NodeType MAP_GENERATOR = new NodeType(15);

	public static NodeType SCRIPT_NODE = new NodeType(16);
	
	NodeType(int val)
	{
		this.val = val;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.val;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeType other = (NodeType) obj;
		if (this.val != other.val)
			return false;
		return true;
	}

	public int toNative() { return this.val; }
	
	@Override
	public String toString()
	{
		return NativeMethods.xnProductionNodeTypeToString(toNative());
	}
	
	public boolean isGenerator()
	{
		return NativeMethods.xnIsTypeGenerator(toNative());
	}
	
	public boolean isDerivedFrom(NodeType base)
	{
		return NativeMethods.xnIsTypeDerivedFrom(toNative(), base.toNative());
	}
	
	private final int val;
}
