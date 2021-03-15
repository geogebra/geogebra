package org.geogebra.web.full.main.embed;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.undo.UndoInfoStoredListener;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ScriptManagerW;

import elemental2.core.Global;

/**
 * Embedded GeoGebra calculator for Notes
 */
public class CalcEmbedElement extends EmbedElement {

	private final GeoGebraFrameFull frame;
	private UndoRedoGlue undoRedoGlue;

	/**
	 * @param widget
	 *            calculator frame
	 */
	public CalcEmbedElement(GeoGebraFrameFull widget, EmbedManagerW embedManager, int embedId) {
		super(widget);
		frame = widget;
		frame.useDataParamBorder();
		setupUndoRedo(embedId, embedManager);
	}

	private void setupUndoRedo(int embedID, EmbedManagerW embedManager) {
		AppW app = frame.getApp();
		app.setUndoRedoPanelAllowed(false);
		app.setUndoRedoEnabled(true);
		Kernel kernel = app.getKernel();
		kernel.setUndoActive(true);

		// Store initial undo info
		kernel.initUndoInfo();

		UndoManager undoManager = kernel.getConstruction().getUndoManager();
		undoRedoGlue = new UndoRedoGlue(embedID, undoManager, embedManager);
	}

	@Override
	public void executeAction(EventType action) {
		undoRedoGlue.executeAction(action);
	}

	@Override
	public void setSize(int contentWidth, int contentHeight) {
		frame.getApp().getGgbApi().setSize(contentWidth, contentHeight);
	}

	@Override
	public String getContentSync() {
		return Global.JSON.stringify(
				frame.getApp().getGgbApi().getFileJSON(false));
	}

	/**
	 * @return API
	 */
	@Override
	public Object getApi() {
		ScriptManagerW sm = (ScriptManagerW) frame.getApp()
				.getScriptManager();
		return sm.getApi();
	}

	@Override
	public void setJsEnabled(boolean jsEnabled) {
		frame.getApp().getScriptManager().setJsEnabled(jsEnabled);
		GuiManagerW guiManager = frame.getApp().getGuiManager();
		if (guiManager != null) {
			guiManager.updatePropertiesView();
		}
	}

	@Override
	public void setContent(String base64) {
		frame.getApp().getGgbApi().setBase64(base64);
	}

	private static class UndoRedoGlue implements UndoInfoStoredListener {

		private int embedId;
		private boolean ignoreUndoInfoStored;
		private UndoManager embeddedUndoManager;
		private EmbedManagerW embedManager;

		private UndoRedoGlue(int embedId, UndoManager embeddedUndoManager,
				EmbedManagerW embedManager) {
			this.embedId = embedId;
			this.ignoreUndoInfoStored = false;
			this.embeddedUndoManager = embeddedUndoManager;
			this.embedManager = embedManager;
			embeddedUndoManager.addUndoInfoStoredListener(this);
		}

		@Override
		public void onUndoInfoStored() {
			if (!ignoreUndoInfoStored) {
				embedManager.createUndoAction(embedId);
			}
		}

		private void executeAction(EventType action) {
			if (EventType.UNDO.equals(action)) {
				undo();
			} else if (EventType.REDO.equals(action)) {
				redo();
			} else if (EventType.EMBEDDED_PRUNE_STATE_LIST.equals(action)) {
				pruneStateList();
			}
		}

		private void undo() {
			ignoreUndoInfoStored = true;
			embeddedUndoManager.undo();
			ignoreUndoInfoStored = false;
		}

		private void redo() {
			ignoreUndoInfoStored = true;
			embeddedUndoManager.redo();
			ignoreUndoInfoStored = false;
		}

		private void pruneStateList() {
			embeddedUndoManager.pruneStateList();
		}
	}
}
