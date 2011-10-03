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
 * $Revision: 1.2 $
 * $Date: 2006-05-05 15:46:09 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.impl;

import org.concord.sensor.DeviceTime;
import org.concord.sensor.LoggingRequest;

public class LoggingRequestImpl extends ExperimentRequestImpl
    implements LoggingRequest
{
    protected int startCondition;
    protected DeviceTime startTime;
    protected int triggerPosition;
    protected int preTriggerSamples;
    protected int triggerChannel;
    protected float triggerValue;
    
    public int getStartCondition()
    {
        return startCondition;
    }

    public DeviceTime getStartTime()
    {
        return startTime;
    }

    public int getTriggerPosition()
    {
        return triggerPosition;
    }

    public int getPreTriggerSamples()
    {
        return preTriggerSamples;
    }

    public int getTriggerChannel()
    {
        return triggerChannel;
    }

    public float getTriggerValue()
    {
        return triggerValue;
    }

}
