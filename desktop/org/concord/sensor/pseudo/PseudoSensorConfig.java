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
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.pseudo;

import org.concord.sensor.device.impl.SensorConfigImpl;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PseudoSensorConfig extends SensorConfigImpl
{
	String name = null;
    private float sinOffset = 5;
    private float sinMagnitude = 10;
	
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#isConfirmed()
	 */
	public boolean isConfirmed() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getPortName()
	 */
	public String getPortName() 
	{
		return "Pseudo Port " + getPort();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getName()
	 */
	public String getName() 
	{
		// FIXME this should take into account the quantity type
		return "PseudoSensor";
	}

    /**
     * @param f
     */
    public void setSinOffset(float f)
    {
        sinOffset = f;        
    }

    public float getSinOffset()
    {
        return sinOffset;
    }
    
    /**
     * @param f
     */
    public void setSinMagnitude(float f)
    {
        sinMagnitude = f;        
    }

    public float getSinMagnitude()
    {
        return sinMagnitude;
    }
}
