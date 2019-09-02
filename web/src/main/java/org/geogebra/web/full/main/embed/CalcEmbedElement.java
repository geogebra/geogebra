package org.geogebra.web.full.main.embed;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.undoredo.UndoInfoStoredListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;

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
		frame.getElement().getStyle().setWidth(contentWidth - 2, Unit.PX);
		frame.getElement().getStyle().setHeight(contentHeight - 2, Unit.PX);
		frame.getApp().checkScaleContainer();
	}

	@Override
	public String getContentSync() {
		return JSON.stringify(
				frame.getApp().getGgbApi().getFileJSON(false));
	}

	/**
	 * @return API
	 */
	public JavaScriptObject getApi() {
		ScriptManagerW sm = (ScriptManagerW) frame.getApp()
				.getScriptManager();
		return sm.getApi();
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
