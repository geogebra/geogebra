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
 * Created on Feb 22, 2005
 *
 */
package org.concord.sensor.contrib;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataProducer;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.SensorDataConsumer;
import org.concord.sensor.SensorDataProducer;

/**
 * simpel implementation of the DataConsumer
 * This class should be moved to the Data package
 * actually I think this class or a similar one is
 * already in the data pacakge
 * 
 * @author Dmitry Markman
 *
 */
public class SimpleSensorDataConsumer implements SensorDataConsumer 
{
protected SensorDataProducer  sensorDataProducer;
protected DataListener        dataListener;
    
    public SimpleSensorDataConsumer(){
    }

    public SensorDataProducer getSensorDataProducer(){
        return sensorDataProducer;
    }

    public void setSensorDataProducer(SensorDataProducer sensorDataProducer){
        this.sensorDataProducer = sensorDataProducer;
    }

	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataConsumer#addDataProducer(org.concord.framework.data.stream.DataProducer)
	 */
	public void addDataProducer(DataProducer source) {
	    if(source instanceof SensorDataProducer){
		    setSensorDataProducer((SensorDataProducer)source);
		    dataListener = new SimpleDataListener();
		    source.addDataListener(dataListener);
        }
	}
	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataConsumer#removeDataProducer(org.concord.framework.data.stream.DataProducer)
	 */
	public void removeDataProducer(DataProducer source) {
	    if(source != sensorDataProducer) return;
	    if(source != null && dataListener != null) source.removeDataListener(dataListener);
	}
}

class SimpleDataListener implements DataListener{
	public void dataReceived(DataStreamEvent dataEvent)
	{
		int numSamples = dataEvent.getNumSamples();
		float [] data = dataEvent.getData();
		if(numSamples > 0) {
			System.out.println("" + numSamples + " " +
						data[0]);
			System.out.flush();
		} 
		else {
			System.out.println("" + numSamples);
		}
	}

	public void dataStreamEvent(DataStreamEvent dataEvent)
	{				
		String eventString;
		int eventType = dataEvent.getType();
		
		if(eventType == 1001) return;
		
		switch(eventType) {
			case DataStreamEvent.DATA_DESC_CHANGED:
				eventString = "Description changed";
			break;
			default:
				eventString = "Unknown event type";					
		}
		
		System.out.println("Data Event: " + eventString); 
	}
}
