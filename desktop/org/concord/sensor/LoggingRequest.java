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
package org.concord.sensor;

public interface LoggingRequest
    extends ExperimentRequest
{
    public static final int START_IMMEDIATELY   = 0;
    public static final int START_AT_TIME       = 1;
    public static final int START_DEVICE_BUTTON = 2;
    public static final int START_ABOVE_TRIGGER = 3;
    public static final int START_RISING_EDGE   = 4;
    public static final int START_BELOW_TRIGGER = 5;
    public static final int START_FALLING_EDGE  = 6;
    
    public int getStartCondition();
    
    /**
     * This is used if the START_AT_TIME is the start condition
     * @return
     */
    public DeviceTime getStartTime();
    
    /**
     * This is a percentage 0-100 where the trigger should be in the 
     * saved data.  0 means all the data should be after the trigger. 
     * 25 means 1/4 of the data should be saved before the trigger.
     * @return
     */
    public int getTriggerPosition();
    
    /**
     * These are the number of samples that should be collected before
     * the trigger is enabled.  This can conflict with the trigger position
     * above.  0 disables the pre triggering.
     * 
     * @return
     */
    public int getPreTriggerSamples();
    
    /**
     * This is the channel that will be monitored for the trigger
     * @return
     */
    public int getTriggerChannel();
    
    /**
     * This is the tigger value how it is used depends on the 
     * start condition.
     * 
     * @return
     */
    public float getTriggerValue();
}
