/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.io.MyXMLio;
import geogebra.main.Application;
import geogebra.util.CopyPaste;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * UndoManager handles undo information for a Construction. 
 * It uses an undo info list with construction snapshots in temporary files.
 * @author Markus Hohenwarter
 */
public class UndoManager {

	private static final String TEMP_FILE_PREFIX = "GeoGebraUndoInfo";

	// maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps
	private static final int MAX_CAPACITY = 100; 

	private Construction construction;
	private LinkedList undoInfoList;	      
	private ListIterator iterator;  // invariant: iterator.previous() is the current state
	private MyXMLio xmlio;

	private Application app;

	/**
	 * Creates a new UndowManager for the given Construction.	 
	 */	
	public UndoManager(Construction c) {				
		construction = c;
		xmlio = new MyXMLio(c.getKernel(), c);	
		c.setXMLio(xmlio);
		undoInfoList = new LinkedList();		

		app = c.getApplication();
	}
	

	private void updateUndoActions() {
		if (app.useFullGui())
			app.getGuiManager().updateActions();		
	}

	/**
	 * Clears undo info list and adds current state to the undo info list.	 
	 */
	synchronized void initUndoInfo() {
		clearUndoInfo();
		storeUndoInfo();
	}       

	private synchronized void clearUndoInfo() {
		undoInfoList.clear();
		iterator = undoInfoList.listIterator();
		System.gc();
	}

	/**
	 * Loads previous construction state from undo info list.
	 */
	public synchronized void undo() {
		
		if (undoPossible()) {		
			iterator.previous();
			loadUndoInfo(iterator.previous());     
			iterator.next();  
			updateUndoActions();
		}				         			     
	}

	/**
	 * Loads next construction state from undo info list.
	 */
	public synchronized void redo() {           
		if (redoPossible()) {
			loadUndoInfo(iterator.next());	  
			updateUndoActions();
		}		   
	}           

	/**
	 * Get current undo info for later comparisons
	 * @return Object (the file of last undo)
	 */
	final public synchronized Object getCurrentUndoInfo() {
		Object ret = iterator.previous();
		iterator.next();
		return ret;
	}

	/**
	 * Reloads construction state at current position of undo list
	 * (this is needed for "cancel" actions).
	 */
	final public synchronized void restoreCurrentUndoInfo() {		
		loadUndoInfo(iterator.previous()); 
		iterator.next();   
		updateUndoActions();
	} 	

	/**
	 * Adds construction state to undo info list
	 */
	public void storeUndoInfoAfterPasteOrAdd() {

		// this can cause a java.lang.OutOfMemoryError for very large constructions
		final StringBuilder currentUndoXML = construction.getCurrentUndoXML();
		
		Thread undoSaverThread = new Thread() {
			public void run() {
				doStoreUndoInfo(currentUndoXML);
				CopyPaste.pastePutDownCallback(app);
				System.gc();
			}
		};
		undoSaverThread.start();

	}

	public void storeUndoInfo() {
		storeUndoInfo(false);
	}
	
	/**
	 * Adds construction state to undo info list.
	 */
	public void storeUndoInfo(final boolean refresh) {	
		
		// this can cause a java.lang.OutOfMemoryError for very large constructions
		final StringBuilder currentUndoXML = construction.getCurrentUndoXML();
		
		Thread undoSaverThread = new Thread() {
			public void run() {
				doStoreUndoInfo(currentUndoXML);
				if (refresh)
					restoreCurrentUndoInfo();
				System.gc();
			}
		};
		undoSaverThread.start();
	}

	private synchronized void doStoreUndoInfo(final StringBuilder undoXML) {			
			// avoid security problems calling from JavaScript ie setUndoPoint()
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					try {			
					
					// perform the security-sensitive operation here
					
					// save to file
					File undoInfo = createTempFile(undoXML);

					// insert undo info 
					iterator.add(undoInfo);				 

					// remove everything after the insert position until end of list
					while (iterator.hasNext()) {
						undoInfo = (File) iterator.next();
						iterator.remove();	
						undoInfo.delete();
					}

					// delete first if too many in list
					if (undoInfoList.size() > MAX_CAPACITY) {                						
						// use iterator to delete to avoid ConcurrentModificationException		
						// go to beginning of list
						while (iterator.hasPrevious())
							undoInfo = (File) iterator.previous();

						iterator.remove();	
						undoInfo.delete();

						while (iterator.hasNext())
							iterator.next();											
					}										

					} 
					catch (Exception e) {		
						Application.debug("storeUndoInfo: " + e.toString());
						e.printStackTrace();
					}     	
					catch (java.lang.OutOfMemoryError err) {
						Application.debug("UndoManager.storeUndoInfo: " + err.toString());
						err.printStackTrace();
						System.gc();
					}
					
					
					
					return null;
				}
			});


		updateUndoActions();	
	}		

	/**
	 * Creates a temporary file containing the zipped undoXML.
	 */
	private synchronized File createTempFile(StringBuilder undoXML) throws IOException {
		// create temp file
		File tempFile = File.createTempFile(TEMP_FILE_PREFIX, ".ggb");
		// Remove when program ends
		tempFile.deleteOnExit();

		// create file
		FileOutputStream fos = new FileOutputStream(tempFile);
		MyXMLio.writeZipped(fos, undoXML); 		
		fos.close();

		return tempFile;
	}

	/**
	 * restore info at position pos of undo list
	 */
	final private synchronized void loadUndoInfo(final Object info) { 
				try {    
					// load from file
					File tempFile = (File) info;
					InputStream is = new FileInputStream(tempFile);	
					
					// make sure objects are displayed in the correct View
					app.setActiveView(Application.VIEW_EUCLIDIAN);

					// load undo info
					app.getScriptManager().disableListeners();
					xmlio.readZipFromMemory(is);					
					app.getScriptManager().enableListeners();
					
					is.close();
				} 
				catch (Exception e) {
					System.err.println("setUndoInfo: " + e.toString());
					e.printStackTrace();      
					restoreCurrentUndoInfo();
				}   
				catch (java.lang.OutOfMemoryError err) {
					System.err.println("UndoManager.loadUndoInfo: " + err.toString());
					System.gc();							
				}

	} 		       

	/**
	 * Returns whether undo operation is possible or not.	 
	 */
	public boolean undoPossible() {  
		if (!app.isUndoActive()) return false;
		return iterator.nextIndex() > 1;	
	}

	/**
	 * Returns whether redo operation is possible or not.	 
	 */
	public boolean redoPossible() {
		if (!app.isUndoActive()) return false;
		return iterator.hasNext();
	}

	/**
	 * Processes xml string. Note: this will change the construction.
	 */
	synchronized void processXML(String strXML) throws Exception {	
		xmlio.processXMLString(strXML, true, false,false);
	}		

}
