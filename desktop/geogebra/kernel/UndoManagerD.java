/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.UndoManager;
import geogebra.common.main.App;
import geogebra.common.util.CopyPaste;
import geogebra.io.MyXMLio;
import geogebra.main.AppD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * UndoManager handles undo information for a Construction. It uses an undo info
 * list with construction snapshots in temporary files.
 * 
 * @author Markus Hohenwarter
 */
public class UndoManagerD extends UndoManager {
	
	/**
	 * Desktop version of ap stat: wrapper for file
	 * @author kondr
	 *
	 */
	protected class AppStateDesktop implements AppState{
		private File f;
		/**
		 * Wrap file into app state
		 * @param f file
		 */
		AppStateDesktop(File f){
			this.f = f;
		}
		/**
		 * Unwrap the file
		 * @return file
		 */
		public File getFile(){
			return f;
		}
		public void delete() {
			f.delete();
			
		}
	}

	private static final String TEMP_FILE_PREFIX = "GeoGebraUndoInfo";
	
	/**
	 * Creates a new UndowManager for the given Construction.
	 * @param cons construction
	 */
	public UndoManagerD(Construction cons) {
		super(cons);
	}

	/**
	 * Adds construction state to undo info list
	 */
	@Override
	public void storeUndoInfoAfterPasteOrAdd() {

		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction.getCurrentUndoXML();

		Thread undoSaverThread = new Thread() {
			@Override
			public void run() {
				doStoreUndoInfo(currentUndoXML);
				CopyPaste.pastePutDownCallback(app);
			}
		};
		undoSaverThread.start();

	}

	
	

	/**
	 * Adds construction state to undo info list.
	 */
	@Override
	public void storeUndoInfo(final boolean refresh) {

		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction.getCurrentUndoXML();

		Thread undoSaverThread = new Thread() {
			@Override
			public void run() {
				doStoreUndoInfo(currentUndoXML);
				if (refresh)
					restoreCurrentUndoInfo();
			}
		};
		undoSaverThread.start();
	}

	/**
	 * Adds construction state to undo info list.
	 * @param undoXML string builder with construction XML
	 */
	synchronized void doStoreUndoInfo(final StringBuilder undoXML) {
		// avoid security problems calling from JavaScript ie setUndoPoint()
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				try {

					// perform the security-sensitive operation here

					// save to file
					File undoInfo = createTempFile(undoXML);

					// insert undo info
					AppState appStateToAdd = new AppStateDesktop(undoInfo);
					iterator.add(appStateToAdd);
					pruneStateList();
					
				} catch (Exception e) {
					App.debug("storeUndoInfo: " + e.toString());
					e.printStackTrace();
				} catch (java.lang.OutOfMemoryError err) {
					App.debug("UndoManager.storeUndoInfo: "
							+ err.toString());
					err.printStackTrace();
				}

				return null;
			}
		});

		updateUndoActions();
	}

	/**
	 * Creates a temporary file containing the zipped undoXML.
	 * @param undoXML XML string
	 * @return temporary file
	 * @throws IOException on file creation problem
	 */
	synchronized static File createTempFile(StringBuilder undoXML)
			throws IOException {
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
	@Override
	final protected synchronized void loadUndoInfo(final AppState info) {
		try {
			// load from file
			File tempFile = ((AppStateDesktop) info).getFile();
			InputStream is = new FileInputStream(tempFile);

			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// load undo info
			((AppD)app).getScriptManager().disableListeners();
			((geogebra.io.MyXMLio)construction.getXMLio()).readZipFromMemory(is);
			((AppD)app).getScriptManager().enableListeners();

			is.close();
		} catch (Exception e) {
			System.err.println("setUndoInfo: " + e.toString());
			e.printStackTrace();
			restoreCurrentUndoInfo();
		} catch (java.lang.OutOfMemoryError err) {
			System.err.println("UndoManager.loadUndoInfo: " + err.toString());
		}

	}



	/**
	 * Processes xml string. Note: this will change the construction.
	 */
	@Override
	public synchronized void processXML(String strXML) throws Exception {
		construction.setFileLoading(true);
		((geogebra.io.MyXMLio)construction.getXMLio()).processXMLString(strXML, true, false, false);
		construction.setFileLoading(false);
	}
	
	

}
