package org.geogebra.web.html5.kernel;

import org.geogebra.common.kernel.AppState;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringAppState;
import org.geogebra.common.kernel.UndoCommand;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.storage.client.Storage;

/**
 * Undo manager using session storage
 */
public class UndoManagerW extends UndoManager {

	/**
	 * can be null (eg IE9 running locally)
	 */
	Storage storage;

	/**
	 * @param cons
	 *            construction
	 */
	public UndoManagerW(Construction cons) {
		super(cons);
		if (Browser.supportsSessionStorage()) {
			storage = Storage.getSessionStorageIfSupported();
		} else {
			Log.warn("Session storage not supported");
		}
	}

	@Override
	public void storeUndoInfoAfterPasteOrAdd() {
		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction
		        .getCurrentUndoXML(true);

		doStoreUndoInfo(currentUndoXML);
		app.getCopyPaste().pastePutDownCallback(app);
	}

	@Override
	public void storeUndoInfo(final StringBuilder currentUndoXML,
			final boolean refresh) {
		doStoreUndoInfo(currentUndoXML);
		if (refresh) {
			restoreCurrentUndoInfo();
		}
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
			String undoXMLString = undoXML.toString();
			AppState appStateToAdd = null;
			if (storage != null) {
				appStateToAdd = new StorageAppState(storage, undoXMLString);
			} else {
				appStateToAdd = new StringAppState(undoXMLString);
			}
			UndoCommand command = new UndoCommand(appStateToAdd, ((AppW) app).getSlideID());
			maybeStoreUndoCommand(command);
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
	protected void loadUndoInfo(final AppState info, String slideID) {
		if (info == null) {
			Log.warn("No undo info.");
			return;
		}
		try {
			app.setActiveSlide(slideID);
			app.getEuclidianView1().setKeepCenter(false);
			// load from file
			String tempXML = info.getXml();
			if (tempXML == null) {
				Log.error("Undo not supported.");
			}
			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// load undo info
			app.getScriptManager().disableListeners();
			processXML(tempXML, false);
			app.getScriptManager().enableListeners();

			AppW appW = (AppW) app;
			if (appW.getPageController() != null) {
				appW.getPageController().updatePreviewImage();
			}
			// the size of the panel we are loading into may have changed since
			// undo point was saved (e.g. keyboard closed, APPS-149)
			appW.updateViewSizes();
		} catch (Exception e) {
			e.printStackTrace();
			Log.debug(e);
			restoreCurrentUndoInfo();
			Log.error("Undo exception:" + e.getMessage());
		} catch (Error err) {
			Log.error("Undo error:" + err.getMessage());
		}
	}
}
