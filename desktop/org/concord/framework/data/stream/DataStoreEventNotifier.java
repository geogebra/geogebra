/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-06-21 17:53:16 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.framework.data.stream;


/**
 * DataStoreEventNotifier
 * Class name and description
 *
 * Date created: Jun 21, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public interface DataStoreEventNotifier
{
	public void notifyDataStoreListeners(DataStoreEvent evt);
}
