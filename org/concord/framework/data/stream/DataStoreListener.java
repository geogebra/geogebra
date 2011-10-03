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
 * $Revision: 1.4 $
 * $Date: 2005-08-05 16:11:10 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.framework.data.stream;


/**
 * DataStoreListener
 * Class name and description
 *
 * Date created: Aug 25, 2004
 *
 * @author imoncada<p>
 *
 */
public interface DataStoreListener
{
	/**
	 * Method called when there is data added to a data store 
	 * @param evt
	 */
	public void dataAdded(DataStoreEvent evt);
	
	/**
	 * Method called when there is data removed from a data store 
	 * @param evt
	 */
	public void dataRemoved(DataStoreEvent evt);
	
	/**
	 * Method called when there is data changed on a data store 
	 * @param evt
	 */
	public void dataChanged(DataStoreEvent evt);

	/**
	 * Method called when there is a change in the data channels description
	 * or a channel has been added or removed 
	 * @param evt
	 */
	public void dataChannelDescChanged(DataStoreEvent evt);
}
