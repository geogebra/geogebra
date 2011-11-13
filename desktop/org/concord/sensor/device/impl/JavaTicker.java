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
package org.concord.sensor.device.impl;

import org.concord.sensor.impl.TickListener;
import org.concord.sensor.impl.Ticker;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JavaTicker extends Thread
	implements Ticker
{
	int millis;
	boolean ticking = false;
	boolean started = false;
	TickListener tickListener;
		
	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#start(int)
	 */
	synchronized public void startTicking(int millis, TickListener listener) 
	{
		this.millis = millis;
		ticking = true;
		if(started) {
			notify();
		} else {
			started = true;
			start();
		}

		if(listener == null) {
		    throw new RuntimeException("Started ticker with null listener");
		}
		
	    // We check if the listener is null here
	    // We want to make sure the no one is expecting a tick
	    // and isn't getting one, so each user of this ticker
	    // needs to set this to null when they are done with it
	    if(tickListener != null){
	        throw new RuntimeException("Inconsitant ticker state");
	    }
	    tickListener = listener;
	}

	synchronized public void stopTicking(TickListener listener) 
	{
		ticking = false;
		
		if(listener != tickListener && tickListener != null) {
		    tickListener.tickStopped();
		}
		
		tickListener = null;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#isTicking()
	 */
	synchronized public boolean isTicking() 
	{
		return ticking;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#setInterfaceManager(org.concord.sensor.InterfaceManager)
	 */
	synchronized public void setTickListener(TickListener tListener) 
	{
	    // We check if the listener is null here
	    // We want to make sure the no one is expecting a tick
	    // and isn't getting one, so each user of this ticker
	    // needs to set this to null when they are done with it
	    if(tListener != null && tickListener != null){
	        throw new RuntimeException("Inconsitant ticker state");
	    }
	    tickListener = tListener;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#getInterfaceManager()
	 */
	synchronized public TickListener getTickListener() 
	{
	    return tickListener;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#createNew()
	 */
	public Ticker createNew() 
	{
		return new JavaTicker();
	}
	
	public int currentTimeMillis()
	{
		return (int)(System.currentTimeMillis() % (long)Integer.MAX_VALUE);
	}
	
	public void run()
	{
		while(true) {
		    synchronized(this) {
		        if(!ticking) {
		            try {
		                wait();
		            } 
		            catch(InterruptedException e) {					
		                e.printStackTrace();
		            }
		        }
		        
		        if(tickListener != null) {
		            tickListener.tick();
		        } else {
		            System.err.println("ticking a null listener");
		        }
		        
		        try {
		            // We wait so that we release the lock
		            wait(millis);										
		        }
		        catch(InterruptedException e) {				
		            e.printStackTrace();
		        }
		    }
		}
	}
}
