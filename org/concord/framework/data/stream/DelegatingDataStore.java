/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-06-29 22:02:01 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.framework.data.stream;

import java.util.Vector;

/**
 * DelegatingDataStore
 * 
 * Receives a data store and delegates all the methods to it
 *
 * Date created: Jun 24, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public class DelegatingDataStore 
	implements DataStore, DataStoreListener, DataStoreEventNotifier
{
	protected DataStore dataStore;
	protected Vector dataStoreListeners;
	
	/**
	 */
	public DelegatingDataStore()
	{
		dataStoreListeners = new Vector();
	}

	/**
	 * @param dataStore
	 */
	public DelegatingDataStore(DataStore dataStore)
	{
		this();
		setDataStore(dataStore);
	}
	
	/**
	 * Delegates to the data store
	 * 
	 * @see org.concord.framework.data.stream.DataStore#getValueAt(int, int)
	 */
	public synchronized Object getValueAt(int numSample, int numChannel) 
	{
		return dataStore.getValueAt(numSample, numChannel); 
	}

	/**
	 * Delegates to the data store
	 * 
	 * @see org.concord.framework.data.stream.DataStore#getTotalNumChannels()
	 */
	public int getTotalNumChannels() 
	{
		return dataStore.getTotalNumChannels();
	}
	
	/**
	 * Delegates to the data store
	 * 
	 * @see org.concord.framework.data.stream.DataStore#getTotalNumSamples()
	 */
	public synchronized int getTotalNumSamples() 
	{
		return dataStore.getTotalNumSamples();
	}

	/**
	 * @see org.concord.framework.data.stream.DataStore#addDataStoreListener(org.concord.framework.data.stream.DataStoreListener)
	 */
	public void addDataStoreListener(DataStoreListener l)
	{
		dataStoreListeners.add(l);
	}

	/**
	 * @see org.concord.framework.data.stream.DataStore#removeDataStoreListener(org.concord.framework.data.stream.DataStoreListener)
	 */
	public void removeDataStoreListener(DataStoreListener l)
	{
		dataStoreListeners.remove(l);
	}

	/**
	 * Delegates to the data store
	 * 
	 * @see org.concord.framework.data.stream.DataStore#clearValues()
	 */
	public void clearValues()
	{
		dataStore.clearValues();
	}
	
	/**
	 * Delegates to the data store
	 * 
	 * @see org.concord.framework.data.stream.DataStore#getDataChannelDescription(int)
	 */
	public DataChannelDescription getDataChannelDescription(int numChannel)
	{
		return dataStore.getDataChannelDescription(numChannel);
	}
	
	/**
	 * 
	 * @return
	 */
	public DataStore getDataStore()
	{
		return dataStore;
	}
	
	/**
	 * 
	 * @param dataStore
	 */
	public void setDataStore(DataStore dataStore)
	{
		if (this.dataStore != null){
			this.dataStore.removeDataStoreListener(this);
		}
		this.dataStore = dataStore;
		if (this.dataStore != null){
			this.dataStore.addDataStoreListener(this);
		}
	}

	/**
	 * @see org.concord.framework.data.stream.DataStoreListener#dataAdded(org.concord.framework.data.stream.DataStoreEvent)
	 */
	public void dataAdded(DataStoreEvent evt)
	{
		//Notify the event to our listeners
		notifyDataStoreListeners(copyEvent(evt));
	}

	/**
	 * @see org.concord.framework.data.stream.DataStoreListener#dataChanged(org.concord.framework.data.stream.DataStoreEvent)
	 */
	public void dataChanged(DataStoreEvent evt)
	{
		//Notify the event to our listeners
		notifyDataStoreListeners(copyEvent(evt));
	}

	/**
	 * @see org.concord.framework.data.stream.DataStoreListener#dataChannelDescChanged(org.concord.framework.data.stream.DataStoreEvent)
	 */
	public void dataChannelDescChanged(DataStoreEvent evt)
	{
		//Notify the event to our listeners
		notifyDataStoreListeners(copyEvent(evt));
	}

	/**
	 * @see org.concord.framework.data.stream.DataStoreListener#dataRemoved(org.concord.framework.data.stream.DataStoreEvent)
	 */
	public void dataRemoved(DataStoreEvent evt)
	{
		//Notify the event to our listeners
		notifyDataStoreListeners(copyEvent(evt));
	}

	/**
	 * @param evt
	 * @return
	 */
	protected DataStoreEvent copyEvent(DataStoreEvent evt)
	{
		DataStoreEvent newEvt = new DataStoreEvent(this, evt.getType());
		return newEvt;
	}
	
	/**
	 * @see org.concord.framework.data.stream.DataStoreEventNotifier#notifyDataStoreListeners(org.concord.framework.data.stream.DataStoreEvent)
	 */
	public void notifyDataStoreListeners(DataStoreEvent evt)
	{
		for (int i=0; i<dataStoreListeners.size(); i++){
			DataStoreListener l = (DataStoreListener)dataStoreListeners.elementAt(i);
			//System.out.println("notifying "+l);
			evt.setSource(this);
			if (evt.getType() == DataStoreEvent.DATA_ADDED){
				l.dataAdded(evt);
			}
			else if (evt.getType() == DataStoreEvent.DATA_CHANGED){
				l.dataChanged(evt);
			}
			else if (evt.getType() == DataStoreEvent.DATA_REMOVED){
				l.dataRemoved(evt);
			}
			else if (evt.getType() == DataStoreEvent.DATA_DESC_CHANGED){
				l.dataChannelDescChanged(evt);
			}
		}
	}
}
