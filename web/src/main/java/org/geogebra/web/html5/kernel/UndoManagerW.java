package org.geogebra.web.html5.kernel;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;

import com.google.gwt.storage.client.Storage;

public class UndoManagerW extends UndoManager {

	private static final String TEMP_STORAGE_PREFIX = "GeoGebraUndoInfo";
	private static long nextKeyNum = 1;

	/**
	 * can be null (eg IE9 running locally)
	 */
	Storage storage;

	protected class AppStateWeb implements AppState {
		private String key;
		private String xml;

		AppStateWeb(String xmls) {
			if (storage != null) {
				storage.setItem(key = TEMP_STORAGE_PREFIX + nextKeyNum++, xmls);
			} else {
				xml = xmls;
			}
		}

		public String getXML() {
			if (storage == null) {
				return xml;
			}
			return storage.getItem(key);
		}

		public void delete() {
			xml = null;
			if (storage != null) {
				storage.removeItem(key);
			}
		}
	}

	public UndoManagerW(Construction cons) {
		super(cons);
		if (Browser.supportsSessionStorage()) {
			storage = Storage.getSessionStorageIfSupported();
		} else {
			Log.warn("Session storage not supported");
		}
	}

	@Override
	public void processXML(String xml) throws Exception {
		construction.getXMLio().processXMLString(xml, true, false, true, false);
	}

	@Override
	public void storeUndoInfoAfterPasteOrAdd() {
		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction
		        .getCurrentUndoXML(true);

		// Thread undoSaverThread = new Thread() {
		// @Override
		// public void run() {
		doStoreUndoInfo(currentUndoXML);
		app.getCopyPaste().pastePutDownCallback(app);
		// }
		// };
		// undoSaverThread.start();
	}

	@Override
	public void storeUndoInfo(final StringBuilder currentUndoXML,
			final boolean refresh) {

		// Thread undoSaverThread = new Thread() {
		// @Override
		// public void run() {

		doStoreUndoInfo(currentUndoXML);
		if (refresh)
			restoreCurrentUndoInfo();

		// }
		// };
		// undoSaverThread.start();

	}

	/**
	 * Adds construction state to undo info list.
	 * 
	 * @param undoXML
	 *            string builder with construction XML
	 */
	synchronized void doStoreUndoInfo(final StringBuilder undoXML) {

		try {
			// insert undo info
			AppState appStateToAdd = new AppStateWeb(undoXML.toString());
			iterator.add(appStateToAdd);
			pruneStateList();
			app.getEventDispatcher().dispatchEvent(
			        new Event(EventType.STOREUNDO, null));

		} catch (Exception e) {
			Log.debug("storeUndoInfo: " + e.toString());
			e.printStackTrace();
		} catch (Error err) {
			Log.debug("UndoManager.storeUndoInfo: " + err.toString());
			err.printStackTrace();
		}
		updateUndoActions();
	}

	@Override
	protected void loadUndoInfo(final AppState info) {
		try {
			// load from file
			String tempXML = ((AppStateWeb) info).getXML();
			if (tempXML == null) {
				Log.error("Undo not supported.");
			}
			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// load undo info
			app.getScriptManager().disableListeners();
			processXML(tempXML);
			app.getScriptManager().enableListeners();
			// If there are Exercises we also have to update the Exercises
			if (app.getKernel().hasExercise()) {
				app.getKernel().getExercise().notifyUpdate();
			}

		} catch (Exception e) {
			e.printStackTrace();
			restoreCurrentUndoInfo();
			Log.error("Undo exception:" + e.getMessage());
		} catch (Error err) {
			Log.error("Undo error:" + err.getMessage());
		}
	}
}
