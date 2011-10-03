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

/**
 * A data store which can have one channel which automatically increments.
 * This class used to be called DeltaDataStore
 * The incremental channel can only be -1 or 0.  
 * 
 * In the future this interface should be deprecated and replace with a mechanism
 * where any channel can be auto incrementing.  That would be stored in the
 * channel description of the data store.  In the meantime this interface is
 * intended to make this as clear as possible.
 * 
 * @author scott
 */
public interface AutoIncrementDataStore extends DataStore
{
	/**
	 * This returns true if the passed in channel is automatically incrementing.
	 * Only -1 and 0 are valid incremental channels.
	 * 
	 * If this returns false then either the data store doesn't have an
	 * incremental channel, or the other channel is the incremental channel.  The
	 * method isAutoIncrementing can be used to check if the data store has
	 * an incremental channel.
	 * 
	 * @return
	 */
	public boolean isIncrementalChannel(int channelIndex);
		
	/**
	 * If this is true then one of the channels in this data store is auto
	 * incremented.  The implementation might actually multiple the sample 
	 * number by the increment.  Simply adding the increment will build up small
	 * rounding errors.  The data store doesn't need to store the actual value.
	 * 
	 * @return 
	 */
	public boolean isAutoIncrementing();
	
	/**
	 * Return the increment used by this data store.  This value is
	 * undefined if isAutoIncrementing() returns false.
	 *   
	 * It would be best in this case to return a Float.NaN
	 * 
	 * Typically this increment is a sample time.  But it could be used for other
	 * things beside time.
	 * 
	 * @return
	 */
	public float getIncrement();
}
