/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

package org.concord.framework.data.stream;

public class DataEvent
{
	public static final int DATA_DESC_CHANGED = 1004;

	public int type;

	public DataStreamDescription dataDesc = null;
	
	public DataEvent()
	{
		this(0);
	}

	public DataEvent(int type)
	{
		this.type = type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public void setDataDescription(DataStreamDescription dataDesc)
	{ 
		this.dataDesc = dataDesc;
	}

	public DataStreamDescription getDataDescription()
	{
		return dataDesc;
	}

}
