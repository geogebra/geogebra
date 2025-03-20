package org.geogebra.web.full.main.embed;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.AlgoTableToChart;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.undo.UndoInfoStoredListener;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ScriptManagerW;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.base.Js;

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
		app.setUndoRedoMode(UndoRedoMode.EXTERNAL);
		Kernel kernel = app.getKernel();
		kernel.setUndoActive(true);

		// Store initial undo info
		kernel.initUndoInfo();

		UndoManager undoManager = kernel.getConstruction().getUndoManager();
		undoRedoGlue = new UndoRedoGlue(embedID, undoManager, embedManager);
	}

	@Override
	public void executeAction(ActionType action) {
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
	public void setJsEnabled(boolean jsEnabled, boolean runningEnabled) {

		frame.getApp().getScriptManager().setJsEnabled(jsEnabled);

		frame.getApp().getEventDispatcher().disableScriptType(ScriptType.JAVASCRIPT);

		GuiManagerW guiManager = frame.getApp().getGuiManager();
		if (guiManager != null) {
			guiManager.updatePropertiesView();
		}
	}

	@Override
	public void setContent(String base64) {
		frame.getApp().getGgbApi().setBase64(base64);
	}

	/**
	 * set the default style for charts in notes
	 * @param isMebis whether mebis is running
	 * @param chartType type of chart (linegraph, barchart, piechart)
	 */
	public void initChart(boolean isMebis, AlgoTableToChart.ChartType chartType) {
		EuclidianView ev = frame.getApp().getActiveEuclidianView();
		GeoElement chart = frame.getApp().getKernel().lookupLabel("chart");

		switch (chartType) {
		case PieChart:
			ev.getSettings().setShowAxes(false);
			ev.setRealWorldCoordSystem(-4, -4, 4, 4);
			break;
		case LineGraph:
			setGrid(EuclidianView.GRID_CARTESIAN);
			ev.getSettings().setShowAxes(true);
			if (isMebis) {
				chart.setObjColor(GColor.newColorRGB(0x00A8D5));
			} else {
				chart.setObjColor(GColor.newColorRGB(0x6557D2));
			}
			chart.setLineThickness(8);
			break;
		case BarChart:
			ev.getSettings().setShowAxes(true);
			if (isMebis) {
				chart.setObjColor(GColor.newColorRGB(0x00A8D5));
			} else {
				chart.setObjColor(GColor.newColorRGB(0x6557D2));
			}
			chart.setAlphaValue(181. / 255);
			break;
		default:
			break;
		}

		chart.setLabelVisible(false);
		chart.updateRepaint();
		frame.getApp().getKernel().initUndoInfo();
	}

	/**
	 * @param cmd command
	 */
	public void sendCommand(String cmd) {
		frame.getApp().getGgbApi().evalCommand(cmd);
	}

	/**
	 * Set the specified axis to positive only with the given crossing
	 * @param axis axis id (0 - x, 1 - y)
	 * @param crossing the value at which the given axis crosses the other
	 */
	public void setGraphAxis(int axis, double crossing) {
		EuclidianSettings evs = frame.getApp().getSettings().getEuclidian(1);
		evs.beginBatch();
		evs.setPositiveAxis(axis, true);
		evs.setAxisCross(axis, crossing);
		evs.endBatch();
		frame.getApp().getKernel().notifyRepaint();
	}

	/**
	 * set grid type for EV
	 * @param grid grid type
	 */
	private void setGrid(int grid) {
		EuclidianSettings evs = frame.getApp().getSettings().getEuclidian(1);
		evs.beginBatch();
		evs.showGrid(true);
		evs.setGridType(grid);
		evs.endBatch();
		frame.getApp().getKernel().notifyRepaint();
	}

	private static class UndoRedoGlue implements UndoInfoStoredListener {

		private final int embedId;
		private final UndoManager embeddedUndoManager;
		private final EmbedManagerW embedManager;

		private UndoRedoGlue(int embedId, UndoManager embeddedUndoManager,
				EmbedManagerW embedManager) {
			this.embedId = embedId;
			this.embeddedUndoManager = embeddedUndoManager;
			this.embedManager = embedManager;
			embeddedUndoManager.addUndoInfoStoredListener(this);
		}

		@Override
		public void onUndoInfoStored() {
			embedManager.createUndoAction(embedId);
		}

		protected void executeAction(ActionType action) {
			if (ActionType.UNDO.equals(action)) {
				undo();
			} else if (ActionType.REDO.equals(action)) {
				redo();
			} else if (ActionType.EMBEDDED_PRUNE_STATE_LIST.equals(action)) {
				pruneStateList();
			}
		}

		private void undo() {
			embeddedUndoManager.undo();
		}

		private void redo() {
			embeddedUndoManager.redo();
		}

		private void pruneStateList() {
			embeddedUndoManager.pruneStateList();
		}
	}

	public GeoGebraFrameFull getFrame() {
		return frame;
	}

	@Override
	public void drawPreview(GGraphics2D g2, int width, int height, double angle) {
		HTMLCanvasElement canvas = Js.uncheckedCast(DomGlobal.document.createElement("canvas"));
		canvas.width = width;
		canvas.height = height;
		try {
			frame.getApp().getGuiManager().getLayout()
					.getDockManager().paintPanels(canvas, null, 1);
		} catch (RuntimeException rte) {
			Log.debug(rte);
		}
		((GGraphics2DW) g2).drawImage(canvas, 0, 0, width, height);
	}
}
