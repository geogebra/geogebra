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

package org.concord.framework.startable;


/**
 * This temporarily extends DataFlow.  To make it easier to do the migration to it.
 * 
 * @author scytacki
 *
 */
public interface Startable{

	/** 
	 * start the startable
	 * should send a startable event
	 */
	void start();

	/** 
	 * stop the startable
	 * should send a startable event
	 */
	void stop();

	/** 
	 * reset the startable
	 * should send a startable event
	 * after the reset isInInitialState should return true.
	 */
	void reset();

	/** 
	 * check if running.
	 */
	boolean isRunning();
	
	/** 
	 * check if the startable is in its initial state.  If a startable
	 * doesn't have an initial state then this should return true all of 
	 * the time. 
	 */
	public boolean isInInitialState();
	
	/**
	 * check if the startable is at the end of its data stream. If true, the startable
	 * can potentially started from its current state. If false, it cannot be started until
	 * it has been reset.
	 * @return
	 */
	public boolean isAtEndOfStream();
	
	/**
	 * @return information about this startable to customize controls for it.
	 */
	public StartableInfo getStartableInfo();
	
	/**
	 * Add a listener to find out when this startable is started, stopped and reset. 
	 * 
	 * @param listener
	 */
	public void addStartableListener(StartableListener listener);
	
	/**
	 * remove a listener
	 * 
	 * @param listener
	 */
	public void removeStartableListener(StartableListener listener);
}
