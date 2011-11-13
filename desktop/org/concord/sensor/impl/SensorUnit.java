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

/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-08-05 18:26:08 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.impl;

import org.concord.framework.data.DataDimension;


/**
 * SensorUnit
 * Class name and description
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public class SensorUnit
	implements DataDimension
{
	String unit;
	
	/**
	 * 
	 */
	public SensorUnit(String unit)
	{
		this.unit = unit;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.data.DataDimension#getDimension()
	 */
	public String getDimension()
	{
		return unit;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.data.DataDimension#setDimension(java.lang.String)
	 */
	public void setDimension(String dimension)
	{
		unit = dimension;
	}
}
