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

import java.util.Vector;

import org.concord.framework.data.DataDimension;
import org.concord.framework.startable.AbstractStartable;
import org.concord.framework.util.Copyable;

public class DefaultDataProducer extends AbstractStartable
	implements DataProducer, Copyable
{
	protected Vector dataListeners = new Vector();

	protected float [] values = new float [1];
	protected DataStreamDescription dataDesc;
	protected DataStreamEvent dataEvent;
	
	protected boolean running = false;
	
	public DefaultDataProducer()
	{
		this(1.0f);
	}

	public DefaultDataProducer(float dt)
	{
		dataDesc = new DataStreamDescription();
		dataDesc.setDt(dt);

		dataEvent = new DataStreamEvent(DataStreamEvent.DATA_RECEIVED, values, null, dataDesc);
		dataEvent.setSource(this);
	}

	public void addDataListener(DataListener listener)
	{
		dataListeners.addElement(listener);
	}
	
	public void removeDataListener(DataListener listener)
	{
		dataListeners.removeElement(listener);
	}

	public DataStreamDescription getDataDescription()
	{
		return dataDesc;
	}

	/**
	 * @see org.concord.framework.startable.Startable#stop()
	 */
	public void stop()
	{
		running = false;
		notifyStopped();
	}

	/**
	 * @see org.concord.framework.startable.Startable#start()
	 */
	public void start()
	{
		running = true;
		notifyStarted(isInInitialState());
	}
	
	/**
	 * @see org.concord.framework.startable.Startable#reset()
	 */
	public void reset()
	{
		running = false;
		notifyReset();
	}
	
	protected void notifyDataStreamEvent(int type)
	{
		dataEvent.setType(type);
		for(int i=0; i<dataListeners.size(); i++) {
			DataListener dataListener = (DataListener)dataListeners.elementAt(i);
			if(dataListener != null)
			{
				dataListener.dataStreamEvent(dataEvent);
			}
		}
		dataEvent.setType(DataStreamEvent.DATA_RECEIVED);
	}

	public void addValue(float value)
	{
		values[0] = value;
		
		notifyDataReceived();
	}

	protected void notifyDataReceived()
	{
		dataEvent.setType(DataStreamEvent.DATA_RECEIVED);
		for(int i=0; i<dataListeners.size(); i++) {
			DataListener dataListener = (DataListener)dataListeners.elementAt(i);
			if(dataListener != null)
			{
				dataListener.dataReceived(dataEvent);
			}
		}
	}
	
	public void setUnit(DataDimension unit)
	{
		dataDesc.getChannelDescription().setUnit(unit);
		notifyDataStreamEvent(DataEvent.DATA_DESC_CHANGED);
	}

	public void setDt(float dt)
	{
		dataDesc.setDt(dt);
		notifyDataStreamEvent(DataEvent.DATA_DESC_CHANGED);
	}
	
	public float getDt() {
	    return dataDesc.getDt();
	}

	public Object getCopy() {
		DefaultDataProducer producer = new DefaultDataProducer();
		producer.dataDesc = (DataStreamDescription)this.dataDesc.getCopy();
		producer.dataEvent = this.dataEvent.clone(new DataStreamEvent());
		//producer.dataListeners = this.dataListeners;
		producer.values = this.values;
		// TODO Auto-generated method stub
		return producer;
	}

	public boolean isRunning() {
		return running;
	}
}
