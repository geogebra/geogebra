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
 * Created on Jun 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.sensor.impl;



/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Ticker 
{
    /**
     * If the passed in listener is not equal to the current 
     * listener then the current listener is notified of this
     * start event
     * 
     * @param millis
     * @param listener
     */
	public void startTicking(int millis, TickListener listener);
	
    /**
     * If the passed in listener is not equal to the current 
     * listener then the current listener is notified of this
     * stop event
     * 
     * @param millis
     * @param listener
     */
	public void stopTicking(TickListener listener);
	
	public boolean isTicking();
	
	public TickListener getTickListener();
	
	/**
	 * This is need because waba can't do reflection. This will
	 * be used to make new copies of this ticker if they are 
	 * needed.
	 * @return
	 */
	public Ticker createNew();
	
	/**
	 * This is needed in a few places
	 * and putting it here allows us to abstract the java waba 
	 * differences
	 * @return
	 */
	public int currentTimeMillis();
}
