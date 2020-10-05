package org.geogebra.web.html5.kernel;

import org.geogebra.common.kernel.AppState;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.DefaultUndoManager;
import org.geogebra.common.kernel.StringAppState;
import org.geogebra.common.kernel.UndoCommand;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;

/**
 * Undo manager using session storage
 */
public class UndoManagerW extends DefaultUndoManager {

	/**
	 * @param cons
	 *            construction
	 */
	public UndoManagerW(Construction cons) {
		super(cons);
	}

	@Override
	protected UndoCommand createUndoCommand(AppState appState) {
		return new UndoCommand(appState, ((AppW) app).getSlideID());
	}

	@Override
	protected AppState extractStateFromFile(String arg) {
		GgbFile file = new GgbFile();
		((AppW) app).getViewW().setFileFromJsonString(arg, file);
		return new StringAppState(file.get("geogebra.xml"));
	}

	@Override
	protected void loadUndoInfo(final AppState state, String slideID) {
		if (state == null) {
			Log.warn("No undo info.");
			return;
		}
		try {
			app.setActiveSlide(slideID);
			app.getEuclidianView1().setKeepCenter(false);
			// load from file
			String tempXML = state.getXml();
			if (tempXML == null) {
				Log.error("Undo not supported.");
			}
			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// load undo info
			app.getScriptManager().disableListeners();
			processXML(tempXML, false);
			app.getScriptManager().enableListeners();

			app.getActiveEuclidianView().invalidateDrawableList();

			AppW appW = (AppW) app;
			if (appW.getPageController() != null) {
				appW.getPageController().updatePreviewImage();
			}
			// the size of the panel we are loading into may have changed since
			// undo point was saved (e.g. keyboard closed, APPS-149)
			appW.updateViewSizes();
		} catch (Throwable t) {
			Log.error("Undo error:" + t.getMessage());
			Log.debug(t);
			restoreCurrentUndoInfo();
		}
	}
}
