package geogebra.web.kernel;

import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.CopyPaste;
import geogebra.web.io.MyXMLio;

import com.google.gwt.storage.client.Storage;

public class UndoManager extends AbstractUndoManager {

	private static final String TEMP_STORAGE_PREFIX = "GeoGebraUndoInfo";
	private static long nextKeyNum = 1;

	Storage storage = Storage.getSessionStorageIfSupported();

	protected class AppStateWeb implements AppState{
		private String key;
		AppStateWeb(String xmls){
			if (storage != null)
				storage.setItem(key = TEMP_STORAGE_PREFIX+nextKeyNum++, xmls);
		}
		public String getXML(){
			if (storage == null)
				return null;
			return storage.getItem(key);
		}
		public void delete() {
			if (storage != null)
				storage.removeItem(key);
		}
	}

	private MyXMLio xmlio;

	public UndoManager(Construction cons) {
	    super(cons);
		xmlio = new MyXMLio(cons.getKernel(), cons);
		cons.setXMLio(xmlio);
    }

	@Override
	public void processXML(String xml) throws Exception {
		xmlio.processXMLString(xml, true, false);

	}

	@Override
	public void storeUndoInfoAfterPasteOrAdd() {
		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction.getCurrentUndoXML();

		//Thread undoSaverThread = new Thread() {
		//	@Override
		//	public void run() {
				doStoreUndoInfo(currentUndoXML);
				CopyPaste.pastePutDownCallback(app);
				System.gc();
		//	}
		//};
		//undoSaverThread.start();
	}

	@Override
	public void storeUndoInfo(final boolean refresh) {

		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction.getCurrentUndoXML();

		//Thread undoSaverThread = new Thread() {
		//	@Override
		//	public void run() {

				doStoreUndoInfo(currentUndoXML);
				if (refresh)
					restoreCurrentUndoInfo();
				System.gc();

		//	}
		//};
		//undoSaverThread.start();

	}

	/**
	 * Adds construction state to undo info list.
	 * @param undoXML string builder with construction XML
	 */
	synchronized void doStoreUndoInfo(final StringBuilder undoXML) {

		try {
			// insert undo info
			AppState appStateToAdd = new AppStateWeb(undoXML.toString());
			iterator.add(appStateToAdd);
			pruneStateList();

		} catch (Exception e) {
			AbstractApplication.debug("storeUndoInfo: " + e.toString());
			e.printStackTrace();
		} catch (Error err) {
			AbstractApplication.debug("UndoManager.storeUndoInfo: "
					+ err.toString());
			err.printStackTrace();
			System.gc();
		}
		updateUndoActions();
	}

	@Override
	protected void loadUndoInfo(final AppState info) {
		try {
			// load from file
			String tempXML = ((AppStateWeb) info).getXML();

			// make sure objects are displayed in the correct View
			app.setActiveView(AbstractApplication.VIEW_EUCLIDIAN);

			// load undo info
			app.getScriptManager().disableListeners();
			processXML(tempXML);
			app.getScriptManager().enableListeners();

		} catch (Exception e) {
			System.err.println("setUndoInfo: " + e.toString());
			e.printStackTrace();
			restoreCurrentUndoInfo();
		} catch (Error err) {
			System.err.println("UndoManager.loadUndoInfo: " + err.toString());
			System.gc();
		}
	}
}
