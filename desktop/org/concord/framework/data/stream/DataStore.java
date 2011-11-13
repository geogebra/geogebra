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
 * $Revision: 1.5 $
 * $Date: 2005-08-05 16:11:10 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.framework.data.stream;


/**
 * DataStore
 * A Data Store is a class that stores data in the form of 
 * samples. Each sample can have different channels.
 * (For example, if the data were to be shown in a data table, 
 * the samples are rows and the channels are columns) 
 * The data stored has a data description also (units, etc)
 *
 * Date created: Aug 23, 2004
 *
 * @author imoncada<p>
 *
 */
public interface DataStore
{
	/**
	 * Returns the total number of samples in the data
	 * (number of sets of values stored)
	 * (For example, if the data is shown in a data table, 
	 * this value corresponds with the number of rows) 
	 * @return total number of samples in the data stored
	 */
	public int getTotalNumSamples();
	
	/**
	 * Returns the total number of channels in the data
	 * (number values in each set of values stored)
	 * (For example, if the data is shown in a data table, 
	 * this value corresponds with the number of columns) 
	 * @return total number of channels in the data stored
	 */
	public int getTotalNumChannels();
	
	/**
	 * Returns the value of a specific sample in a specific
	 * channel
	 * @return data value stored at the sample and channel specified
	 */
	public Object getValueAt(int numSample, int numChannel);

	/**
	 * Add a listener to the data store
	 * @param l
	 */
	public void addDataStoreListener(DataStoreListener l);
	
	/**
	 * Remove a listener from the data store
	 * @param l
	 */
	public void removeDataStoreListener(DataStoreListener l);
	
	/**
	 * 
	 * @param numChannel
	 * @return
	 */
	public DataChannelDescription getDataChannelDescription(int numChannel);
	
	/**
	 * Clears all values in the data store
	 */
	public void clearValues();
}
