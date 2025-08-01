package org.geogebra.web.html5.kernel;

import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.main.undo.AppState;
import org.geogebra.common.main.undo.DefaultUndoManager;
import org.geogebra.common.main.undo.StringAppState;
import org.geogebra.common.main.undo.UndoCommand;
import org.geogebra.common.main.undo.UndoHistory;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.PageListControllerInterface;

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
		return new UndoCommand(appState, app.getSlideID());
	}

	@Override
	protected AppState extractStateFromFile(String arg) {
		GgbFile file = new GgbFile();
		((AppW) app).getArchiveLoader().setFileFromJsonString(arg, file);
		return new StringAppState(file.get("geogebra.xml").string);
	}

	@Override
	protected void loadUndoInfo(final AppState state, String slideID) {
		if (state == null) {
			Log.warn("No undo info.");
			return;
		}
		try {
			app.setActiveSlide(slideID);
			// load from file
			String tempXML = state.getXml();
			if (tempXML == null) {
				Log.error("Undo not supported.");
			}
			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// load undo info
			app.getEventDispatcher().disableListeners();
			construction.processXML(tempXML, false, null);
			app.getEventDispatcher().enableListeners();

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

	@Override
	public void runAfterSlideLoaded(String slideID, Runnable action) {
		OpenFileListener callback = () -> {
			action.run();
			updatePreviewCard(slideID);
			return true;
		};
		if (slideID != null && !slideID.equals(app.getSlideID())) {
			app.registerOpenFileListener(callback);
			((AppW) app).getPageController().clickPage(slideID);
		} else {
			callback.onOpenFile();
		}
	}

	private void updatePreviewCard(String slideId) {
		PageListControllerInterface pageController = ((AppW) app).getPageController();
		if (pageController != null) {
			pageController.updatePreviewImage(slideId);
		}
	}

	@Override
	public void replayActions(final String slideID, @Nonnull final UndoCommand until) {
		super.replayActions(slideID, until);
		updatePreviewCard(slideID);
	}

	@Override
	public void undoHistoryFrom(Map<String, UndoHistory> undoHistory) {
		app.getEventDispatcher().disableListeners();
		super.undoHistoryFrom(undoHistory);
		app.getEventDispatcher().enableListeners();
		app.getActiveEuclidianView().invalidateDrawableList();
	}

	@Override
	public void executeAction(ActionType action, String... args) {
		super.executeAction(action, args);
		PageListControllerInterface pageController = ((AppW) app).getPageController();
		if (pageController != null) {
			pageController.updatePreviewImage();
		}
	}
}
