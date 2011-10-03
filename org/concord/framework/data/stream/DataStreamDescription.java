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

package org.concord.framework.data.stream;

public class DataStreamDescription
{
	/**
	 * This set of data uses the dt because it is one after the
	 * other in time: sequencial data
	 */  
	public final static int DATA_SEQUENCE = 0;
	
	/**
	 * This set of data is a collection of points that 
	 * does not need to be sequencial in time.
	 */ 
	public final static int DATA_SERIES = 1;

	private int dataType = DATA_SEQUENCE;

	private float dt;
	private int dataOffset = 0;
	private int nextSampleOffset = -1;
	
	private DataChannelDescription [] channelDescriptions = null;	//DataChannelDescription objects
	private DataChannelDescription dtChannelDescription;

	public DataStreamDescription(){
		this(0.0f,1);
	}

	public DataStreamDescription(float dt,int chPerSample){
		this.dt = dt;
		setChannelsPerSample(chPerSample);
		
		//Dt channel description
		dtChannelDescription =  new DataChannelDescription();
		dtChannelDescription.setName("dt");
		dtChannelDescription.setPrecision(2);			
	}
	
	public void setDt(float dt)
	{
		this.dt = dt;
	}

	public float getDt()
	{
		return dt;
	}

	/**
	 * This sets the number of channels per sample
	 * Warning: It will reset all the channel descriptions to null.  And
	 *  then create a new channel description for each channel.
	 * 
	 * @param chPerSample
	 */
	public void	setChannelsPerSample(int chPerSample)
	{
		if(channelDescriptions == null ||
				chPerSample != channelDescriptions.length) {
			channelDescriptions = new DataChannelDescription [chPerSample];

			//Make sure we have at least one channel description
			for (int i = 0; i < chPerSample; i++) {
			    DataChannelDescription channelDesc = new DataChannelDescription();
			    channelDescriptions[i] = channelDesc;
			}

		}
	}

	public int getChannelsPerSample()
	{
		return channelDescriptions.length;
	}

	/**
	 * Use DATA_SEQUENCE or DATA_SERIES here
	 * @param dataType
	 */
	public void	setDataType(int dataType)
	{
		this.dataType = dataType;
	}

	public int getDataType()
	{
		return dataType;
	}

	public void setDataOffset(int dataOffset)
	{
		this.dataOffset = dataOffset;
	}

	public int getDataOffset()
	{
		return dataOffset;
	}

	public void setNextSampleOffset(int next)
	{
		nextSampleOffset = next;
	}

	/** 
	 * This returns how much the index must be incremented to 
	 * get to the next sample.
	 * @return
	 */
	public int getNextSampleOffset()
	{
		if (nextSampleOffset == -1){
			return channelDescriptions.length;
		}
		return nextSampleOffset;
	}
	
	/**
	 * @return Returns the channelDesc.
	 */
	public DataChannelDescription getChannelDescription()
	{
		return getChannelDescription(0);
	}
	
	
	/**
	 * 
	 * @return Returns the channelDesc.
	 */
	public DataChannelDescription getChannelDescription(int index)
	{
		// FIXME: channelDescriptions do not always get set up correctly.
		// Most functions that request them work fine if returned null.
		// Returning null instead of throwing an exception seems to solve a
		// number of problems, though a deeper solution would be better. -SF
		if (index < 0 || index >= channelDescriptions.length) {
//			throw new IndexOutOfBoundsException("channel index: " + index);
			return null;
		}
		return channelDescriptions[index];
	}

	/**
	 * @param channelDesc The channelDesc to set.  This put the description
	 * at position 0 replacing anything that was there.
	 */
	public void setChannelDescription(DataChannelDescription channelDesc)
	{
		if (channelDescriptions.length < 1) return;
		channelDescriptions[0] = channelDesc;
	}
		
	/**
	 * @param channelDesc The channelDesc to set.
	 * @param index the index of the channel that
	 * this channel description describes
	 */
	public void setChannelDescription(DataChannelDescription channelDesc, int index)
	{
		if (index < 0 || index >= channelDescriptions.length) {
				throw new IndexOutOfBoundsException("channel index: " + index);
		}
		channelDescriptions[index] = channelDesc;
	}
	
	/**
	 * @return Returns the dtChannelDescription.
	 */
	public DataChannelDescription getDtChannelDescription()
	{
		return dtChannelDescription;
	}
	
	/**
	 * @param dtChannelDescription The dtChannelDescription to set.
	 */
	public void setDtChannelDescription(DataChannelDescription dtChannelDescription)
	{
		this.dtChannelDescription = dtChannelDescription;
	}
	
	public Object getCopy() {
		DataStreamDescription desc = new DataStreamDescription();
		DataChannelDescription[] channels = 
			new DataChannelDescription[this.channelDescriptions.length];
		for(int i = 0; i < channels.length; i++) {
			channels[i] = 
				(DataChannelDescription)(this.channelDescriptions[i].getCopy());
		}
		desc.channelDescriptions = channels;
		desc.setChannelDescription((DataChannelDescription)this.dtChannelDescription.getCopy());
		desc.setDataType(this.dataType);
		desc.setDt(this.dt);
		desc.setDataOffset(this.dataOffset);
		return desc;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj)
	{
		if(!(obj instanceof DataStreamDescription)){
			return false;
		}
		
		DataStreamDescription dDesc = (DataStreamDescription) obj;
		
		if(dataType != dDesc.dataType ||
				!floatEquals(dt,dDesc.dt) ||
				dataOffset != dDesc.dataOffset){
			return false;
		}
				
		if(dtChannelDescription == null &&
				dDesc.dtChannelDescription != null){
			return false;
		}				
		
		if(dtChannelDescription != null &&
				!dtChannelDescription.equals(dDesc.dtChannelDescription)){
			return false;
		}
		
		if(channelDescriptions == null &&
				dDesc.channelDescriptions != null){
			return false;
		}
						
		if(channelDescriptions != null){
			if(dDesc.channelDescriptions == null){
				return false;
			}
						
			for(int i=0; i<channelDescriptions.length; i++){
				if(i >= dDesc.channelDescriptions.length){
					return false;
				}
				
				if(!channelDescriptions[i].equals(dDesc.channelDescriptions[i])){
					return false;
				}
			}
			
		}
		
		return true;
	}
	
	/**
	 * This is separated out so it can be handled by Waba.  Waba will
	 * most likely not implement Float.compare correctly.
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public final static boolean floatEquals(float f1, float f2)
	{
		return Float.compare(f1, f2) == 0;
	}
	

}
