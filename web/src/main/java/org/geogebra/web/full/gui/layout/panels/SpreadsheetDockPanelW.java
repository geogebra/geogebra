package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetStyleBarW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetViewW;
import org.geogebra.web.full.gui.view.spreadsheet.TableCanvasExporter;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view
 *
 */
public class SpreadsheetDockPanelW extends NavigableDockPanelW {

	private SpreadsheetStyleBarW sstylebar;
	private SpreadsheetViewW sview;
	private AbsolutePanel wrapview;

	/**
	 * @param appl
	 *            application
	 */
	public SpreadsheetDockPanelW(App appl) {
		super(App.VIEW_SPREADSHEET,	getDefaultToolbar(), true);
		app = (AppW) appl;
	}

	@Override
	protected Panel getViewPanel() {
		if (wrapview == null) {
			wrapview = new AbsolutePanel();
			wrapview.addStyleName("SpreadsheetWrapView");
			sview = (SpreadsheetViewW) app.getGuiManager().getSpreadsheetView();
			wrapview.add(sview.getFocusPanel());
		}
		return wrapview;
	}

	@Override
	protected Widget loadStyleBar() {
		if (sstylebar == null) {
			sstylebar = sview.getSpreadsheetStyleBar();
		}
		return sstylebar;
	}

	@Override
	public void onResize() {
		super.onResize();

		if (app != null && sview != null) {
			int width = getComponentInteriorWidth();
			int height = getComponentInteriorHeight();

			if (width <= 0 || height <= 0) {
				return;
			}

			wrapview.setPixelSize(width, height);
			sview.onResize(width, height);
		}
	}

	private static String getDefaultToolbar() {

		return EuclidianConstants.MODE_MOVE
				+ " || "
				+ EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS
				+ " || "
				+ EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE
				+ " || "
				+ EuclidianConstants.MODE_SPREADSHEET_SUM
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_AVERAGE
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_COUNT
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_MAX
				+ " , "
				+ EuclidianConstants.MODE_SPREADSHEET_MIN;
	}

	@Override 
	public boolean isStyleBarVisible() {
		if (app.isApplet() && !app.showMenuBar() && !app.isStyleBarAllowed()) {
			return false;
		}
		return super.isStyleBarVisible();
	}

	@Override
	public boolean hasStyleBar() {
		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();

		if (settings == null) {
			return super.hasStyleBar();
		}

		return super.hasStyleBar() && settings.showRowHeader() && settings.showColumnHeader();
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_spreadsheet();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_spreadsheetView();
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return this.sview.getSpreadsheetTable()
				.getEditor().getTextfield();
	}

	@Override
	public void paintToCanvas(CanvasRenderingContext2D context2d,
			Runnable callback, int left, int top) {
		drawWhiteBackground(context2d, left, top);
		context2d.save();
		context2d.rect(left, top, getOffsetWidth(), getOffsetHeight());
		context2d.clip();
		TableCanvasExporter tableCanvasExporter = new TableCanvasExporter(
				sview.getSpreadsheetTable(), app, getOffsetWidth(), getOffsetHeight(), context2d);
		tableCanvasExporter.paintToCanvas(left, top);
		context2d.restore();
		callback.run();
	}

}
