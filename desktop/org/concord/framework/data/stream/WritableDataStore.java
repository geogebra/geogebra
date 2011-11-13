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
 * $Revision: 1.7 $
 * $Date: 2005-08-05 16:11:10 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.framework.data.stream;


/**
 * WritableDataStore
 * This is a Data Store with data that can be modified
 *
 * Date created: Oct 25, 2004
 *
 * @author imoncada<p>
 *
 */
public interface WritableDataStore extends DataStore
{	
	/**
	 * Sets the value in a specific sample in a specific
	 * channel
	 * @param numChannel	channel number, starting from 0, >0
	 * @param numSample		sample number, starting from 0, >0
	 * @param value			value to add
	 */
	public void setValueAt(int numSample, int numChannel, Object value);

	/**
	 * Removes values in a specific sample in all channels
	 * @param numSample		sample number, starting from 0, >0
	 */
	public void removeSampleAt(int numSample);
	
	/**
	 * Inserts empty values in a specific sample in all channels
	 * @param numSample 	sample number, starting from 0, >0
	 */
	public void insertSampleAt(int numSample);
	
	/**
	 * set the description of the channel.  This includes the label
	 * of the channel, the units, the precision...
	 * 
	 * @param numChannel
	 * @param desc
	 */
	public void setDataChannelDescription(int channelIndex, DataChannelDescription desc);

}
