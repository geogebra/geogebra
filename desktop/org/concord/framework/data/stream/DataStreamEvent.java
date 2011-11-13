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
 * $Date: 2005-08-05 16:11:10 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
 */
package org.concord.framework.data.stream;

/**
 * DataStreamEvent Class name and description
 * 
 * Date created: Aug 24, 2004
 * 
 * @author imoncada
 *         <p>
 * 
 */
public class DataStreamEvent extends DataEvent {
	public static final int DATA_RECEIVED = 1000;
	// this is legacy from waba when things were single threaded and the data producer 
	// needed to notify data listeners so they'd have a chance to update themselves
	// because these are static and final the ids shouldn't change 
	// public static final int DATA_COLLECTING = 1001;
	public static final int DATA_DESC_CHANGED = 1004;
	public static final int DATA_DESC_RESET = 1005;
	public static final int DATA_DESC_ERROR = 1006;
	public static final int DATA_REPLACED = 1007; // new data replaces old, e.g.
													// for soundDataProducer
	public float[] data = null;
	public int numSamples = 1;
	public int[] intData = null;
	public float refVal = 0;

	// hooks for storing profile data
	// as the event is processed timestamps might be stored here which
	// could be logged latter. This would be an ideal use for AspectJ
	// public int [] pTimes = new int [10];
	// public int numPTimes = 0;

	Object source;
	Object additionalInfo;

	public DataStreamEvent() {
		this(DATA_RECEIVED, null, null, null);
	}

	public DataStreamEvent(int type) {
		this(type, null, null, null);
	}

	public DataStreamEvent(int type, float[] data,
			DataStreamDescription dataDesc) {
		this(type, data, null, dataDesc);
	}

	public DataStreamEvent(int type, float[] data, int[] intData,
			DataStreamDescription dataDesc) {
		this.type = type;
		this.data = data;
		this.intData = intData;
		this.dataDesc = dataDesc;
	}

	public void setData(float[] data) {
		this.data = data;
	}

	public float[] getData() {
		return data;
	}

	public void setIntData(int[] data) {
		this.intData = data;
	}

	public int[] getIntData() {
		return intData;
	}

	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}

	public int getNumSamples() {
		return numSamples;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public Object getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Object additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	/**
	 * Copy this object into the passed in object and return it. If the passed
	 * in object is null then it will create a new object
	 * 
	 * @param output
	 * @return
	 */
	public DataStreamEvent clone(DataStreamEvent output) {
		if (output == null) {
			output = new DataStreamEvent();
		}

		output.setData(getData());
		output.setIntData(getIntData());
		output.setNumSamples(getNumSamples());
		output.setSource(getSource());
		output.setAdditionalInfo(getAdditionalInfo());
		output.setType(getType());
		output.setDataDescription(getDataDescription());

		return output;
	}
}
