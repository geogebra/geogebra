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
 * $Revision: 1.11 $
 * $Date: 2007-09-24 18:36:49 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.framework.data.stream;

import java.util.Vector;


/**
 * DefaultDataStore
 * This class is a default implementation of the WritableDataStore
 * interface. 
 *
 * Date created: Oct 24, 2004
 *
 * @author imoncada<p>
 *
 */
public class DefaultDataStore extends AbstractDataStore
	implements WritableArrayDataStore
{
    public float dt = Float.NaN;
    
	public DefaultDataStore()
	{
		super();
	}
	
	/**
	 * @see org.concord.framework.data.stream.DataStore#getValueAt(int, int)
	 */
	public Object getValueAt(int numSample, int numChannel)
	{
		//Special case: when dt is a channel, it's the channel -1
		if (isIncrementalChannel(numChannel)){
			return new Float(numSample * dt);
		}
		
		return super.getValueAt(numSample, numChannel);
	}
	
	/**
	 * Sets a value at the sample and channel indicated
	 * This method adds samples and channels if necessary,
	 * so the intermediate values will be empty
	 * 
	 * @param numChannel	channel number, starting from 0, >0
	 * @param numSample		sample number, starting from 0, >0
	 * @param value			value to add
	 */
	public void setValueAt(int numSample, int numChannel, Object value)
	{
		boolean channelAdded = false;
		boolean valueAdded = false;
		if (numSample < 0 || numChannel < 0) return;
		
		//Locate the channel
		while (numChannel >= channelsValues.size()){
			//Add empty vectors until the desired channel
			channelsValues.addElement(new Vector());
			channelAdded = true;
		}
		Vector channel = (Vector)channelsValues.elementAt(numChannel);
		
		//Locate the sample within the channel
		while (numSample >= channel.size()){
			//Add empty elements until the desired sample
			channel.addElement(null);
		}
		
		if (numSample >= getTotalNumSamples()){
			valueAdded = true;
		}
		
		//Set the value
		channel.setElementAt(value, numSample);
		
		if (channelAdded){
			notifyChannelDescChanged();
		}
		if (valueAdded){
			notifyDataAdded();
		}
		else{
			notifyDataChanged();
		}
	}

	/**
	 * Very inefficient way of saving an array of data points
	 * 
	 */
	public void setValues(int numbChannels,float []values,
	        int offset, int length, int nextSampleOffset)
	{
	    // System.err.println("setValues: " + values.length);
	    // ignore numbChannels for now
	    if(channelsValues.size() == 0) {
	        channelsValues.add(new Vector());
	    }
	    
		Vector channel = (Vector)channelsValues.elementAt(0);		
		
	    for(int i=0;i<length;i++) {
			//Locate the sample within the channel
			while (i >= channel.size()){
				//Add empty elements until the desired sample
				channel.addElement(null);
			}
            
			//Set the value
			channel.setElementAt(new Float(values[offset+(i*nextSampleOffset)]), i);			
	    }
	    
		if (length > getTotalNumSamples()){
			notifyDataAdded();
		} else {
			notifyDataChanged();		    
		}	
	}
		
	/**
	 * @see org.concord.framework.data.stream.WritableDataStore#removeSampleAt(int)
	 */
	public void removeSampleAt(int numSample)
	{
		boolean valueRemoved = false;
		
		if (numSample < 0) return;
		
		for (int i=0; i < channelsValues.size(); i++){
			Vector channel = (Vector)channelsValues.elementAt(i);
			
			if (numSample < channel.size()){
				channel.remove(numSample);
				valueRemoved = true;
			}
		}
		
		if (valueRemoved){
			notifyDataRemoved();
		}
	}
	
	/**
	 * @param i
	 */
	public void insertSampleAt(int i)
	{
		for (int j=0; j < channelsValues.size(); j++){
			Vector channel = (Vector)channelsValues.elementAt(j);
			channel.add(i, null);
		}
	}
	
	/**
     * @see org.concord.framework.data.stream.WritableArrayDataStore#setDt(float)
     */
    public void setDt(float dt)
    {
        this.dt = dt;
		notifyChannelDescChanged();
    }
    
    /**
     * @see org.concord.framework.data.stream.AutoIncrementDataStore#isAutoIncrementing()
     */
    public boolean isAutoIncrementing()
    {
        return !Float.isNaN(dt);
    }
    
    /**
     * Keep this method here in case scripts are using it
     */
    public float getDt()
    {
    	return getIncrement();
    }
    
    public float getIncrement()
    {
        return dt;
    }

    /**
     * If this data store has a dt or increment then that is available 
     * as channel -1. 
     * 
     */
	public boolean isIncrementalChannel(int channelIndex) 
	{
		return channelIndex == -1 && isAutoIncrementing();
	}
}
